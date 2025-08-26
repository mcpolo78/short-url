package com.linkshortener.controller;

import com.linkshortener.dto.CreateLinkRequest;
import com.linkshortener.dto.LinkAnalyticsResponse;
import com.linkshortener.dto.LinkResponse;
import com.linkshortener.service.AnalyticsService;
import com.linkshortener.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/links")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class LinkController {
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @PostMapping
    public ResponseEntity<LinkResponse> createLink(
            @Valid @RequestBody CreateLinkRequest request,
            Authentication authentication) {
        
        try {
            LinkResponse response;
            if (authentication != null && authentication.isAuthenticated()) {
                // Authenticated user
                response = linkService.createLink(request, getUserFromAuthentication(authentication));
            } else {
                // Anonymous user
                response = linkService.createAnonymousLink(request);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<LinkResponse>> getUserLinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (authentication != null && authentication.isAuthenticated()) {
            // Authenticated user - get their links
            Long userId = getUserIdFromAuthentication(authentication);
            Page<LinkResponse> links = linkService.getUserLinks(userId, pageable);
            return ResponseEntity.ok(links);
        } else {
            // Anonymous user - get all public links
            Page<LinkResponse> links = linkService.getAllPublicLinks(pageable);
            return ResponseEntity.ok(links);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LinkResponse> getLink(
            @PathVariable Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Long userId = getUserIdFromAuthentication(authentication);
        Optional<LinkResponse> link = linkService.getLinkById(id, userId);
        
        return link.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LinkResponse> updateLink(
            @PathVariable Long id,
            @Valid @RequestBody CreateLinkRequest request,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            LinkResponse response = linkService.updateLink(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(
            @PathVariable Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            linkService.deleteLink(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleLinkStatus(
            @PathVariable Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            linkService.toggleLinkStatus(id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<LinkResponse>> searchLinks(
            @RequestParam String q,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<LinkResponse> links = linkService.searchUserLinks(userId, q);
        return ResponseEntity.ok(links);
    }
    
    @GetMapping("/top")
    public ResponseEntity<List<LinkResponse>> getTopLinks(
            @RequestParam(defaultValue = "5") int limit,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<LinkResponse> links = linkService.getUserTopLinks(userId, limit);
        return ResponseEntity.ok(links);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<LinkResponse>> getRecentLinks(
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<LinkResponse> links = linkService.getUserRecentLinks(userId, days);
        return ResponseEntity.ok(links);
    }
    
    @GetMapping("/{id}/analytics")
    public ResponseEntity<LinkAnalyticsResponse> getLinkAnalytics(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            LinkAnalyticsResponse analytics = analyticsService.getLinkAnalytics(id, userId, days);
            return ResponseEntity.ok(analytics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/bulk")
    public ResponseEntity<List<LinkResponse>> bulkCreateLinks(
            @RequestBody List<CreateLinkRequest> requests,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<LinkResponse> responses = requests.stream()
                    .map(request -> {
                        try {
                            return linkService.createLink(request, getUserFromAuthentication(authentication));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to create link: " + e.getMessage());
                        }
                    })
                    .toList();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // This will need to be implemented based on your authentication system
        // For now, returning a placeholder
        return 1L;
    }
    
    private com.linkshortener.entity.User getUserFromAuthentication(Authentication authentication) {
        // This will need to be implemented based on your authentication system
        // For now, returning null (will create anonymous links)
        return null;
    }
}
