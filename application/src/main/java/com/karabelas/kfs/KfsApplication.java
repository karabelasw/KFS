package com.karabelas.kfs;


import com.karabelas.kfs.config.StorageProperties;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class KfsApplication {

    private static final Logger log = LoggerFactory.getLogger(KfsApplication.class);

    public static void main(String[] args) {
    	ApplicationContext ctx =  SpringApplication.run(KfsApplication.class, args);
        log.info("KFS starting up...");
        log.info("Let's inspect the beans provided by Spring Boot:");	
        String[] beanNames = ctx.getBeanDefinitionNames();
		log.info("Working directory: " + System.getProperty("java.home"));
		Arrays.sort(beanNames);
		if(beanNames != null) {
			log.info("There are " + beanNames.length + " beans provided by Spring Boot");
	      }
	    for (String beanName : beanNames) {
	    	log.info("------------------------------------------------------");
	    	log.info(beanName);
	    	log.info("------------------------------------------------------");
	    }
        log.info("KFS started successfully.");
    }
}