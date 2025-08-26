package com.linkshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateLinkRequest {
    
    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String originalUrl;
    
    @Size(max = 20, message = "Custom alias must be at most 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias can only contain letters, numbers, underscores and hyphens")
    private String customAlias;
    
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;
    
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;
    
    private LocalDateTime expiresAt;
    
    private String password;
    
    // Constructors
    public CreateLinkRequest() {}
    
    public CreateLinkRequest(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    // Getters and Setters
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    
    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
