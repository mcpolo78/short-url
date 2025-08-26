package com.linkshortener.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        // Use simple in-memory cache instead of Redis for development
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("links");
        return cacheManager;
    }
}
