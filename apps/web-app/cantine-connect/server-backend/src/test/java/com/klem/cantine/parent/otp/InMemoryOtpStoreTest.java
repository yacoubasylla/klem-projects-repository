package com.klem.cantine.parent.otp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOtpStoreTest {

    private final InMemoryOtpStore store = new InMemoryOtpStore();

    @Test
    void verifierEtInvalider_codeCorrect_retourneLEmailAssocie() {
        store.enregistrer("+225700000001", "123456", "parent@example.com");

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).contains("parent@example.com");
    }

    @Test
    void verifierEtInvalider_estUsageUnique_echoueAuDeuxiemeAppel() {
        store.enregistrer("+225700000001", "123456", "parent@example.com");
        store.verifierEtInvalider("+225700000001", "123456");

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).isEmpty();
    }

    @Test
    void verifierEtInvalider_codeIncorrect_retourneVide() {
        store.enregistrer("+225700000001", "123456", "parent@example.com");

        assertThat(store.verifierEtInvalider("+225700000001", "000000")).isEmpty();
    }

    @Test
    void verifierEtInvalider_cleInconnue_retourneVide() {
        assertThat(store.verifierEtInvalider("+225799999999", "123456")).isEmpty();
    }

    @Test
    void verifierEtInvalider_depasseLeNombreMaxDeTentatives_invalideMemeAvecLeBonCode() {
        store.enregistrer("+225700000001", "123456", "parent@example.com");
        for (int i = 0; i < 5; i++) {
            store.verifierEtInvalider("+225700000001", "000000");
        }

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).isEmpty();
    }

    @Test
    void enregistrer_remplaceUnCodePrecedentPourLaMemeCle() {
        store.enregistrer("+225700000001", "111111", "parent@example.com");
        store.enregistrer("+225700000001", "222222", "parent@example.com");

        assertThat(store.verifierEtInvalider("+225700000001", "111111")).isEmpty();
        assertThat(store.verifierEtInvalider("+225700000001", "222222")).contains("parent@example.com");
    }
}
