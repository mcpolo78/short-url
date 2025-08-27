package com.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class LinkShortenerApplication {

    private static final Logger logger = LoggerFactory.getLogger(LinkShortenerApplication.class);

    public static void main(String[] args) {
        logger.info("=== RAILWAY DEBUG: Starting minimal app ===");
        logger.info("Java version: {}", System.getProperty("java.version"));
        logger.info("PORT env: {}", System.getenv("PORT"));
        logger.info("Active profiles: {}", System.getProperty("spring.profiles.active"));
        
        try {
            SpringApplication.run(LinkShortenerApplication.class, args);
            logger.info("=== RAILWAY DEBUG: App started successfully! ===");
        } catch (Exception e) {
            logger.error("=== RAILWAY DEBUG: Startup failed ===", e);
            throw e;
        }
    }
}
