package com.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class LinkShortenerApplication {

    private static final Logger logger = LoggerFactory.getLogger(LinkShortenerApplication.class);

    public static void main(String[] args) {
        logger.info("=== RAILWAY: Starting Link Shortener with DATABASE ===");
        logger.info("Java version: {}", System.getProperty("java.version"));
        logger.info("PORT env: {}", System.getenv("PORT"));
        logger.info("MYSQL_URL: {}", System.getenv("MYSQL_URL") != null ? "SET" : "NOT SET");
        
        try {
            SpringApplication.run(LinkShortenerApplication.class, args);
            logger.info("=== RAILWAY: App with DATABASE started successfully! ===");
        } catch (Exception e) {
            logger.error("=== RAILWAY: Startup failed ===", e);
            throw e;
        }
    }
}
