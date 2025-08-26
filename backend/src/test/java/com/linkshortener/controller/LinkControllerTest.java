package com.linkshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkshortener.dto.CreateLinkRequest;
import com.linkshortener.dto.LinkResponse;
import com.linkshortener.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class LinkControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private LinkResponse testLinkResponse;
    private CreateLinkRequest testRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        testLinkResponse = new LinkResponse();
        testLinkResponse.setId(1L);
        testLinkResponse.setOriginalUrl("https://example.com");
        testLinkResponse.setShortCode("abc123");
        testLinkResponse.setShortUrl("http://localhost:8080/abc123");
        testLinkResponse.setClickCount(0L);
        testLinkResponse.setIsActive(true);
        testLinkResponse.setCreatedAt(LocalDateTime.now());

        testRequest = new CreateLinkRequest();
        testRequest.setOriginalUrl("https://example.com");
        testRequest.setTitle("Test Title");
        testRequest.setDescription("Test Description");
    }

    @Test
    public void testCreateLink() throws Exception {
        // Given
        when(linkService.createAnonymousLink(any(CreateLinkRequest.class)))
                .thenReturn(testLinkResponse);

        // When & Then
        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }
}
