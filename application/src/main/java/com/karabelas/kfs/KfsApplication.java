package com.karabelas.kfs;

import com.karabelas.kfs.config.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class KfsApplication {

    private static final Logger log = LoggerFactory.getLogger(KfsApplication.class);

    public static void main(String[] args) {
        log.info("KFS starting up...");
        SpringApplication.run(KfsApplication.class, args);
        log.info("KFS started successfully.");
    }
}