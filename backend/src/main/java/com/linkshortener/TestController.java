package com.linkshortener;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class TestController {
    
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("RAILWAY WORKS! App running on port: " + System.getenv("PORT"));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test OK - " + System.currentTimeMillis());
    }
}
