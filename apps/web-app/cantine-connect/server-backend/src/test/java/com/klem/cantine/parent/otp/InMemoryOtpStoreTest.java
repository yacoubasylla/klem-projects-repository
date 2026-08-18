package com.klem.cantine.parent.otp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOtpStoreTest {

    private final InMemoryOtpStore store = new InMemoryOtpStore();

    @Test
    void verifierEtInvalider_codeCorrect_retourneVrai() {
        store.enregistrer("+225700000001", "123456");

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).isTrue();
    }

    @Test
    void verifierEtInvalider_estUsageUnique_echoueAuDeuxiemeAppel() {
        store.enregistrer("+225700000001", "123456");
        store.verifierEtInvalider("+225700000001", "123456");

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).isFalse();
    }

    @Test
    void verifierEtInvalider_codeIncorrect_retourneFaux() {
        store.enregistrer("+225700000001", "123456");

        assertThat(store.verifierEtInvalider("+225700000001", "000000")).isFalse();
    }

    @Test
    void verifierEtInvalider_cleInconnue_retourneFaux() {
        assertThat(store.verifierEtInvalider("+225799999999", "123456")).isFalse();
    }

    @Test
    void verifierEtInvalider_depasseLeNombreMaxDeTentatives_invalideMemeAvecLeBonCode() {
        store.enregistrer("+225700000001", "123456");
        for (int i = 0; i < 5; i++) {
            store.verifierEtInvalider("+225700000001", "000000");
        }

        assertThat(store.verifierEtInvalider("+225700000001", "123456")).isFalse();
    }

    @Test
    void enregistrer_remplaceUnCodePrecedentPourLaMemeCle() {
        store.enregistrer("+225700000001", "111111");
        store.enregistrer("+225700000001", "222222");

        assertThat(store.verifierEtInvalider("+225700000001", "111111")).isFalse();
        assertThat(store.verifierEtInvalider("+225700000001", "222222")).isTrue();
    }
}
