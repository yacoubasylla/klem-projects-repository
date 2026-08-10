package com.klem.coreapi.audit.infrastructure.messaging;

import com.klem.coreapi.tenant.application.service.TenantService;
import com.klem.coreapi.tenant.domain.model.Tenant;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Preuve de bout en bout : un appel réel à {@code TenantService.createTenant} — pas de mock —
 * produit un vrai message Kafka sur le topic {@code tenant.created}, contre un broker Kafka réel
 * (Testcontainers) et un PostgreSQL réel (le service reste transactionnel, le message ne doit
 * apparaître qu'après commit — voir {@code PortfolioEventPublisher}, {@code AFTER_COMMIT}).
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
    private TenantService tenantService;

    private Consumer<String, String> consumer;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void createTenant_publishes_a_real_message_on_the_tenant_created_topic() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-portfolio-events", "true", kafka.getBootstrapServers());
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);
        consumerProps.put("auto.offset.reset", "earliest");
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProps);
        consumer.subscribe(java.util.List.of("tenant.created"));
        consumer.poll(Duration.ofMillis(0)); // force l'assignation de partition avant le send

        Tenant tenant = tenantService.createTenant("Boutiki pilote", "commerce informel");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "tenant.created", Duration.ofSeconds(10));

        assertThat(record.key()).isEqualTo(tenant.getId().toString());
        assertThat(record.value())
                .contains("\"eventType\":\"tenant.created\"")
                .contains("\"source\":\"core-api\"")
                .contains("\"aggregateType\":\"tenant\"")
                .contains(tenant.getId().toString())
                .contains("Boutiki pilote");
    }
}
