package com.klem.referentielapi.shared.infrastructure.messaging;

import com.klem.referentielapi.textereglementaire.application.service.TexteReglementaireService;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Preuve de bout en bout : un appel réel à {@code TexteReglementaireService.propose} — pas de mock
 * — produit un vrai message Kafka sur le topic {@code texteReglementaire.proposed}, contre un
 * broker Kafka réel (Testcontainers) et un PostgreSQL réel (le service reste transactionnel, le
 * message ne doit apparaître qu'après commit — voir {@link PortfolioEventPublisher},
 * {@code AFTER_COMMIT}). Même patron que {@code core-api}'s
 * {@code PortfolioEventPublisherIntegrationTest} — non exécuté avec succès dans ce bac à sable
 * (même limitation Docker API 1.32 vs 1.40 documentée sur les autres tests Testcontainers de ce
 * service), à vérifier en CI comme le reste.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PortfolioEventPublisherIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private TexteReglementaireService texteReglementaireService;

    private Consumer<String, String> consumer;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void propose_publishes_a_real_message_on_the_texteReglementaire_proposed_topic() {
        // Ordre réel de KafkaTestUtils.consumerProps : (brokerAddresses, group, autoCommit) — pas
        // (group, autoCommit, brokers). Bug constaté par exécution réelle en CI : le broker se
        // retrouvait affecté à enable.auto.commit, provoquant un ConfigException au démarrage du
        // consumer.
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-portfolio-events", "true");
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);
        consumerProps.put("auto.offset.reset", "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of("texteReglementaire.proposed"));
        consumer.poll(Duration.ofMillis(0)); // force l'assignation de partition avant le send

        TexteReglementaire texte = texteReglementaireService.propose(
                "Note de procédure import véhicules", "circulaire", null, "REF-2026-001",
                "import", "https://douanes.ci/notes/2026-001", "editeur-1");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
                consumer, "texteReglementaire.proposed", Duration.ofSeconds(10));

        assertThat(record.key()).isEqualTo(texte.getId().toString());
        assertThat(record.value())
                .contains("\"eventType\":\"texteReglementaire.proposed\"")
                .contains("\"source\":\"referentiel-api-service\"")
                .contains("\"aggregateType\":\"texteReglementaire\"")
                .contains(texte.getId().toString())
                .contains("Note de procédure import véhicules");
    }
}
