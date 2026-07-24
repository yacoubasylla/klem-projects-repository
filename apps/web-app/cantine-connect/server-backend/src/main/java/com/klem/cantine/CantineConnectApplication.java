package com.klem.cantine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync   // Pour la persistance asynchrone de l'ActionLog via AOP
@EnableScheduling  // Pour les rappels de paiement planifiés (J-7, J-3, J-1)
public class CantineConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CantineConnectApplication.class, args);
    }
}
