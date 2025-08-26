package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.service.AnalyticsService;
import com.linkshortener.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class RedirectController {
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/{code}")
    public ResponseEntity<?> redirectToOriginalUrl(
            @PathVariable String code,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {
        
        Optional<Link> linkOpt = linkService.findByCode(code);
        
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Link not found or has expired");
        }
        
        Link link = linkOpt.get();
        
        // Check if link requires password
        if (link.getPasswordProtected()) {
            if (password == null || !linkService.verifyLinkPassword(link, password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Password required or incorrect");
            }
        }
        
        // Record the click for analytics
        try {
            analyticsService.recordClick(link, request);
        } catch (Exception e) {
            // Log error but don't fail the redirect
            System.err.println("Failed to record click: " + e.getMessage());
        }
        
        // Redirect to original URL
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", link.getOriginalUrl());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
    
    @GetMapping("/{code}/info")
    public ResponseEntity<?> getLinkInfo(@PathVariable String code) {
        Optional<Link> linkOpt = linkService.findByCode(code);
        
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Link not found or has expired");
        }
        
        Link link = linkOpt.get();
        
        // Return basic link information (without revealing password protection details)
        return ResponseEntity.ok().body(Map.of(
                "originalUrl", link.getOriginalUrl(),
                "title", link.getTitle() != null ? link.getTitle() : "",
                "description", link.getDescription() != null ? link.getDescription() : "",
                "createdAt", link.getCreatedAt(),
                "passwordProtected", link.getPasswordProtected()
        ));
    }
    
    @PostMapping("/{code}/verify-password")
    public ResponseEntity<?> verifyPassword(
            @PathVariable String code,
            @RequestBody Map<String, String> request) {
        
        Optional<Link> linkOpt = linkService.findByCode(code);
        
        if (linkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Link not found or has expired");
        }
        
        Link link = linkOpt.get();
        String password = request.get("password");
        
        if (!link.getPasswordProtected()) {
            return ResponseEntity.ok().body(Map.of("valid", true));
        }
        
        boolean isValid = linkService.verifyLinkPassword(link, password);
        return ResponseEntity.ok().body(Map.of("valid", isValid));
    }
}
