package com.linkshortener;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String root() {
        return "Railway Test OK - App is running!";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Test endpoint working!";
    }
}
