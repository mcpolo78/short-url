package com.linkshortener;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String root() {
        return "Railway Test OK - App is running! Port: " + System.getenv("PORT");
    }
    
    @GetMapping("/test")
    public String test() {
        return "Test endpoint working!";
    }
    
    @GetMapping("/env")
    public Map<String, String> env() {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("PORT", System.getenv("PORT"));
        envVars.put("JAVA_VERSION", System.getProperty("java.version"));
        envVars.put("SPRING_PROFILES_ACTIVE", System.getProperty("spring.profiles.active"));
        envVars.put("SERVER_PORT", System.getProperty("server.port"));
        envVars.put("RAILWAY_ENVIRONMENT", System.getenv("RAILWAY_ENVIRONMENT"));
        envVars.put("RAILWAY_SERVICE_NAME", System.getenv("RAILWAY_SERVICE_NAME"));
        return envVars;
    }
}
