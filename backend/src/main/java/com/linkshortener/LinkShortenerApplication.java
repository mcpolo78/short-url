package com.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class LinkShortenerApplication {

    private static final Logger logger = LoggerFactory.getLogger(LinkShortenerApplication.class);
    
    @Value("${server.port:8080}")
    private String serverPort;

    public static void main(String[] args) {
        logger.info("🚀 Starting Link Shortener Application...");
        try {
            SpringApplication.run(LinkShortenerApplication.class, args);
            logger.info("✅ Application started successfully!");
        } catch (Exception e) {
            logger.error("❌ Failed to start application: ", e);
            System.exit(1);
        }
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        logger.info("🎯 Link Shortener API ready on port {}", serverPort);
        logger.info("📡 Health check available at /health");
        logger.info("🔗 API endpoints available at /api/*");
    }
}
