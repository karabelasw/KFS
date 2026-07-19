package com.karabelas.kfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KfsApplication {

    private static final Logger log = LoggerFactory.getLogger(KfsApplication.class);

    public static void main(String[] args) {
        log.info("KFS starting up...");
        SpringApplication.run(KfsApplication.class, args);
        log.info("KFS started successfully.");
    }
}