package com.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class LinkShortenerApplication {

    private static final Logger logger = LoggerFactory.getLogger(LinkShortenerApplication.class);

    public static void main(String[] args) {
        logger.info("=== RAILWAY DEBUG: Ultra minimal startup ===");
        logger.info("Port: {}", System.getenv("PORT"));
        logger.info("Java: {}", System.getProperty("java.version"));
        
        try {
            SpringApplication.run(LinkShortenerApplication.class, args);
            logger.info("=== RAILWAY DEBUG: Startup complete ===");
        } catch (Exception e) {
            logger.error("=== RAILWAY DEBUG: FAILED ===", e);
            throw e;
        }
    }
}
