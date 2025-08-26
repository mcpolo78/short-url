package com.linkshortener.service;

import com.linkshortener.dto.CreateLinkRequest;
import com.linkshortener.dto.LinkResponse;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import com.linkshortener.repository.LinkRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LinkService {
    
    @Autowired
    private LinkRepository linkRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.short-url-length:8}")
    private int shortUrlLength;
    
    public LinkResponse createLink(CreateLinkRequest request, User user) {
        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setTitle(request.getTitle());
        link.setDescription(request.getDescription());
        link.setExpiresAt(request.getExpiresAt());
        link.setUser(user);
        
        // Handle custom alias or generate short code
        if (request.getCustomAlias() != null && !request.getCustomAlias().trim().isEmpty()) {
            String customAlias = request.getCustomAlias().trim();
            if (linkRepository.existsByCustomAlias(customAlias)) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
            link.setCustomAlias(customAlias);
            link.setShortCode(generateUniqueShortCode());
        } else {
            link.setShortCode(generateUniqueShortCode());
        }
        
        // Handle password protection
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            link.setPasswordProtected(true);
            link.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        link = linkRepository.save(link);
        
        // Generate QR code asynchronously
        try {
            String qrCodePath = qrCodeService.generateQRCode(buildShortUrl(link), link.getId());
            link.setQrCodePath(qrCodePath);
            link = linkRepository.save(link);
        } catch (Exception e) {
            // Log error but don't fail the link creation
            System.err.println("Failed to generate QR code: " + e.getMessage());
        }
        
        return convertToResponse(link);
    }
    
    public LinkResponse createAnonymousLink(CreateLinkRequest request) {
        return createLink(request, null);
    }
    
    @Cacheable(value = "links", key = "#code")
    public Optional<Link> findByCode(String code) {
        // Try to find by custom alias first, then by short code
        Optional<Link> link = linkRepository.findByCustomAlias(code);
        if (link.isEmpty()) {
            link = linkRepository.findByShortCode(code);
        }
        
        // Check if link is active and not expired
        if (link.isPresent()) {
            Link l = link.get();
            if (!l.getIsActive() || l.isExpired()) {
                return Optional.empty();
            }
        }
        
        return link;
    }
    
    public Optional<Link> findById(Long id) {
        return linkRepository.findById(id);
    }
    
    @CacheEvict(value = "links", key = "#link.shortCode")
    public void incrementClickCount(Link link) {
        link.incrementClickCount();
        linkRepository.save(link);
    }
    
    public Page<LinkResponse> getUserLinks(Long userId, Pageable pageable) {
        Page<Link> links = linkRepository.findByUserId(userId, pageable);
        return links.map(this::convertToResponse);
    }
    
    public Page<LinkResponse> getAllPublicLinks(Pageable pageable) {
        Page<Link> links = linkRepository.findByIsActiveTrue(pageable);
        return links.map(this::convertToResponse);
    }
    
    public List<LinkResponse> getUserTopLinks(Long userId, int limit) {
        List<Link> links = linkRepository.findByUserIdOrderByClickCountDesc(userId);
        return links.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<LinkResponse> getUserRecentLinks(Long userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Link> links = linkRepository.findRecentLinksByUser(userId, since);
        return links.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public Optional<LinkResponse> getLinkById(Long id, Long userId) {
        Optional<Link> link = linkRepository.findById(id);
        if (link.isPresent() && link.get().getUser() != null && 
            link.get().getUser().getId().equals(userId)) {
            return Optional.of(convertToResponse(link.get()));
        }
        return Optional.empty();
    }
    
    @CacheEvict(value = "links", key = "#link.shortCode")
    public LinkResponse updateLink(Long id, CreateLinkRequest request, Long userId) {
        Optional<Link> optionalLink = linkRepository.findById(id);
        if (optionalLink.isEmpty() || 
            !optionalLink.get().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Link not found or access denied");
        }
        
        Link link = optionalLink.get();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setTitle(request.getTitle());
        link.setDescription(request.getDescription());
        link.setExpiresAt(request.getExpiresAt());
        
        // Handle custom alias update
        if (request.getCustomAlias() != null && !request.getCustomAlias().trim().isEmpty()) {
            String customAlias = request.getCustomAlias().trim();
            if (!customAlias.equals(link.getCustomAlias()) && 
                linkRepository.existsByCustomAlias(customAlias)) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
            link.setCustomAlias(customAlias);
        } else {
            link.setCustomAlias(null);
        }
        
        // Handle password update
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            link.setPasswordProtected(true);
            link.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            link.setPasswordProtected(false);
            link.setPasswordHash(null);
        }
        
        link = linkRepository.save(link);
        return convertToResponse(link);
    }
    
    @CacheEvict(value = "links", key = "#shortCode")
    public void deleteLink(Long id, Long userId) {
        Optional<Link> link = linkRepository.findById(id);
        if (link.isPresent() && link.get().getUser() != null && 
            link.get().getUser().getId().equals(userId)) {
            linkRepository.delete(link.get());
        } else {
            throw new IllegalArgumentException("Link not found or access denied");
        }
    }
    
    @CacheEvict(value = "links", key = "#shortCode")
    public void toggleLinkStatus(Long id, Long userId) {
        Optional<Link> optionalLink = linkRepository.findById(id);
        if (optionalLink.isEmpty() || 
            !optionalLink.get().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Link not found or access denied");
        }
        
        Link link = optionalLink.get();
        link.setIsActive(!link.getIsActive());
        linkRepository.save(link);
    }
    
    public List<LinkResponse> searchUserLinks(Long userId, String keyword) {
        List<Link> links = linkRepository.searchLinksByUser(userId, keyword);
        return links.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public boolean verifyLinkPassword(Link link, String password) {
        if (!link.getPasswordProtected() || link.getPasswordHash() == null) {
            return true;
        }
        return passwordEncoder.matches(password, link.getPasswordHash());
    }
    
    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = RandomStringUtils.randomAlphanumeric(shortUrlLength);
        } while (linkRepository.existsByShortCode(shortCode));
        return shortCode;
    }
    
    private String buildShortUrl(Link link) {
        String code = link.getCustomAlias() != null ? link.getCustomAlias() : link.getShortCode();
        return baseUrl + "/" + code;
    }
    
    private LinkResponse convertToResponse(Link link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setOriginalUrl(link.getOriginalUrl());
        response.setShortCode(link.getShortCode());
        response.setCustomAlias(link.getCustomAlias());
        response.setShortUrl(buildShortUrl(link));
        response.setTitle(link.getTitle());
        response.setDescription(link.getDescription());
        response.setClickCount(link.getClickCount());
        response.setIsActive(link.getIsActive());
        response.setExpiresAt(link.getExpiresAt());
        response.setPasswordProtected(link.getPasswordProtected());
        response.setCreatedAt(link.getCreatedAt());
        response.setUpdatedAt(link.getUpdatedAt());
        
        if (link.getQrCodePath() != null) {
            response.setQrCodeUrl(baseUrl + "/api/qr/" + link.getId());
        }
        
        return response;
    }
}
