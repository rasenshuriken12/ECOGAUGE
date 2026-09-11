package com.rupam.ecogauge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the EcoGauge Spring Boot application.
 * The @SpringBootApplication annotation enables:
 * 1. Autoconfiguration: Spring attempts to automatically configure your application
 * based on the dependencies you have added (e.g., H2, JPA).
 * 2. Component Scanning: Looks for other components (like Controllers, Services, Repositories)
 * in the current package and sub-packages.
 */
@SpringBootApplication
public class EcoGaugeApplication {

    public static void main(String[] args) {
        // This static method runs the Spring application.
        SpringApplication.run(EcoGaugeApplication.class, args);
    }

}
