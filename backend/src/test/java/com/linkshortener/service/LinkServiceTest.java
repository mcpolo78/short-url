package com.linkshortener.service;

import com.linkshortener.dto.CreateLinkRequest;
import com.linkshortener.dto.LinkResponse;
import com.linkshortener.entity.Link;
import com.linkshortener.entity.User;
import com.linkshortener.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private LinkService linkService;

    private Link testLink;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testLink = new Link();
        testLink.setId(1L);
        testLink.setOriginalUrl("https://example.com");
        testLink.setShortCode("abc123");
        testLink.setClickCount(0L);
        testLink.setCreatedAt(LocalDateTime.now());
        testLink.setUser(testUser);
        testLink.setIsActive(true);
    }

    @Test
    public void testCreateLink() throws Exception {
        // Given
        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://example.com");
        request.setCustomAlias("custom");

        when(linkRepository.existsByCustomAlias("custom")).thenReturn(false);
        when(linkRepository.existsByShortCode(anyString())).thenReturn(false);
        when(linkRepository.save(any(Link.class))).thenReturn(testLink);
        when(qrCodeService.generateQRCode(anyString(), any(Long.class))).thenReturn("/qr/path");

        // When
        LinkResponse result = linkService.createLink(request, testUser);

        // Then
        assertNotNull(result);
        assertEquals("https://example.com", result.getOriginalUrl());
        assertEquals("abc123", result.getShortCode());
        assertEquals(0L, result.getClickCount());
        assertNotNull(result.getCreatedAt());

        verify(linkRepository, times(2)).save(any(Link.class)); // Once for initial save, once for QR code path
        verify(qrCodeService, times(1)).generateQRCode(anyString(), any(Long.class));
    }

    @Test
    public void testCreateLinkWithDuplicateAlias() {
        // Given
        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://example.com");
        request.setCustomAlias("custom");
        
        when(linkRepository.existsByCustomAlias("custom")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            linkService.createLink(request, testUser);
        });

        verify(linkRepository, times(1)).existsByCustomAlias("custom");
        verify(linkRepository, never()).save(any(Link.class));
    }

    @Test
    public void testGetUserLinks() {
        // Given
        Link link2 = new Link();
        link2.setId(2L);
        link2.setOriginalUrl("https://google.com");
        link2.setShortCode("def456");
        link2.setClickCount(5L);
        link2.setCreatedAt(LocalDateTime.now());
        link2.setUser(testUser);
        link2.setIsActive(true);

        List<Link> mockLinks = Arrays.asList(testLink, link2);
        Page<Link> mockPage = new PageImpl<>(mockLinks);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(linkRepository.findByUserId(1L, pageable)).thenReturn(mockPage);

        // When
        Page<LinkResponse> result = linkService.getUserLinks(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("abc123", result.getContent().get(0).getShortCode());
        assertEquals("def456", result.getContent().get(1).getShortCode());

        verify(linkRepository, times(1)).findByUserId(1L, pageable);
    }

    @Test
    public void testGetLinkById() {
        // Given
        Long linkId = 1L;
        Long userId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(testLink));

        // When
        Optional<LinkResponse> result = linkService.getLinkById(linkId, userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(linkId, result.get().getId());
        assertEquals("abc123", result.get().getShortCode());

        verify(linkRepository, times(1)).findById(linkId);
    }

    @Test
    public void testGetLinkByIdNotFound() {
        // Given
        Long linkId = 999L;
        Long userId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.empty());

        // When
        Optional<LinkResponse> result = linkService.getLinkById(linkId, userId);

        // Then
        assertFalse(result.isPresent());
        verify(linkRepository, times(1)).findById(linkId);
    }

    @Test
    public void testDeleteLink() {
        // Given
        Long linkId = 1L;
        Long userId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(testLink));

        // When
        linkService.deleteLink(linkId, userId);

        // Then
        verify(linkRepository, times(1)).findById(linkId);
        verify(linkRepository, times(1)).delete(testLink);
    }

    @Test
    public void testIncrementClickCount() {
        // Given
        when(linkRepository.save(testLink)).thenReturn(testLink);

        // When
        linkService.incrementClickCount(testLink);

        // Then
        assertEquals(1L, testLink.getClickCount());
        verify(linkRepository, times(1)).save(testLink);
    }

    @Test
    public void testFindByCode() {
        // Given
        String code = "abc123";
        when(linkRepository.findByCustomAlias(code)).thenReturn(Optional.empty());
        when(linkRepository.findByShortCode(code)).thenReturn(Optional.of(testLink));

        // When
        Optional<Link> result = linkService.findByCode(code);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testLink, result.get());
        verify(linkRepository, times(1)).findByCustomAlias(code);
        verify(linkRepository, times(1)).findByShortCode(code);
    }

    @Test
    public void testFindByCodeWithExpiredLink() {
        // Given
        String code = "abc123";
        testLink.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired
        
        when(linkRepository.findByCustomAlias(code)).thenReturn(Optional.empty());
        when(linkRepository.findByShortCode(code)).thenReturn(Optional.of(testLink));

        // When
        Optional<Link> result = linkService.findByCode(code);

        // Then
        assertFalse(result.isPresent());
    }
}
