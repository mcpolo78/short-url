package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.repository.ClickRepository;
import com.linkshortener.repository.LinkRepository;
import com.linkshortener.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class DashboardController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private LinkRepository linkRepository;
    
    @Autowired
    private ClickRepository clickRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            // Version simplifiée pour le développement
            Map<String, Object> stats = new HashMap<>();
            
            // Statistiques de base
            stats.put("totalLinks", linkRepository.count());
            stats.put("totalClicks", clickRepository.count());
            stats.put("clicksToday", 0L);
            stats.put("uniqueVisitors", 0L);
            
            // Données de test pour les graphiques
            List<Map<String, Object>> dailyClicks = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                dayData.put("clicks", (int)(Math.random() * 50));
                dailyClicks.add(dayData);
            }
            stats.put("dailyClicks", dailyClicks);
            
            // Données horaires
            List<Map<String, Object>> hourlyClicks = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", i + "h");
                hourData.put("clicks", (int)(Math.random() * 20));
                hourlyClicks.add(hourData);
            }
            stats.put("hourlyClicks", hourlyClicks);
            
            // Top liens (les liens existants)
            List<Link> allLinks = linkRepository.findAll();
            stats.put("topLinks", allLinks.stream().limit(5).collect(Collectors.toList()));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Log de l'erreur pour debug
            System.err.println("Erreur dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Stats par défaut en cas d'erreur
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalLinks", 0L);
            defaultStats.put("totalClicks", 0L);
            defaultStats.put("clicksToday", 0L);
            defaultStats.put("uniqueVisitors", 0L);
            defaultStats.put("dailyClicks", new ArrayList<>());
            defaultStats.put("hourlyClicks", new ArrayList<>());
            defaultStats.put("topLinks", new ArrayList<>());
            
            return ResponseEntity.ok(defaultStats);
        }
    }
}
