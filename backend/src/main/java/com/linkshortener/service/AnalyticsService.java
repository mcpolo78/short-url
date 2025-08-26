package com.linkshortener.service;

import com.linkshortener.dto.LinkAnalyticsResponse;
import com.linkshortener.entity.Click;
import com.linkshortener.entity.Link;
import com.linkshortener.repository.ClickRepository;
import com.linkshortener.repository.LinkRepository;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {
    
    @Autowired
    private ClickRepository clickRepository;
    
    @Autowired
    private LinkRepository linkRepository;
    
    @Autowired
    private LinkService linkService;
    
    @Value("${geoip.database-path:}")
    private String geoIpDatabasePath;
    
    private DatabaseReader geoIpReader;
    
    public void recordClick(Link link, HttpServletRequest request) {
        Click click = new Click();
        click.setLink(link);
        click.setIpAddress(getClientIpAddress(request));
        click.setUserAgent(request.getHeader("User-Agent"));
        click.setReferer(request.getHeader("Referer"));
        
        // Parse user agent
        parseUserAgent(click);
        
        // Get geographic information
        getGeographicInfo(click);
        
        // Detect if it's a bot
        detectBot(click);
        
        // Save click record
        clickRepository.save(click);
        
        // Update link click count
        linkService.incrementClickCount(link);
    }
    
    public LinkAnalyticsResponse getLinkAnalytics(Long linkId, Long userId, int days) {
        Optional<Link> linkOpt = linkRepository.findById(linkId);
        if (linkOpt.isEmpty() || (userId != null && !linkOpt.get().getUser().getId().equals(userId))) {
            throw new IllegalArgumentException("Link not found or access denied");
        }
        
        Link link = linkOpt.get();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        
        LinkAnalyticsResponse analytics = new LinkAnalyticsResponse();
        analytics.setLinkId(linkId);
        analytics.setShortCode(link.getShortCode());
        analytics.setOriginalUrl(link.getOriginalUrl());
        analytics.setCreatedAt(link.getCreatedAt());
        
        // Get basic stats
        List<Click> allClicks = clickRepository.findByLinkIdOrderByClickedAtDesc(linkId);
        analytics.setTotalClicks((long) allClicks.size());
        
        // Calculate unique clicks (by IP address)
        Set<String> uniqueIps = allClicks.stream()
                .map(Click::getIpAddress)
                .collect(Collectors.toSet());
        analytics.setUniqueClicks((long) uniqueIps.size());
        
        // Get last click time
        if (!allClicks.isEmpty()) {
            analytics.setLastClickAt(allClicks.get(0).getClickedAt());
        }
        
        // Filter recent clicks
        List<Click> recentClicks = allClicks.stream()
                .filter(click -> click.getClickedAt().isAfter(since))
                .collect(Collectors.toList());
        
        // Daily clicks
        analytics.setDailyClicks(getDailyClickStats(linkId, since));
        
        // Hourly clicks
        analytics.setHourlyClicks(getHourlyClickStats(recentClicks));
        
        // Geographic stats
        analytics.setClicksByCountry(getCountryStats(recentClicks));
        analytics.setClicksByCity(getCityStats(recentClicks));
        
        // Technology stats
        analytics.setClicksByBrowser(getBrowserStats(recentClicks));
        analytics.setClicksByOS(getOSStats(recentClicks));
        analytics.setClicksByDevice(getDeviceStats(recentClicks));
        
        // Referrer stats
        analytics.setClicksByReferrer(getReferrerStats(recentClicks));
        
        // Device type breakdown
        analytics.setDeviceTypeBreakdown(getDeviceTypeBreakdown(recentClicks));
        
        // Bot detection stats
        long botClicks = recentClicks.stream().mapToLong(click -> click.getIsBot() ? 1 : 0).sum();
        analytics.setBotClicks(botClicks);
        analytics.setRealClicks((long) recentClicks.size() - botClicks);
        
        return analytics;
    }
    
    public Map<String, Object> getUserDashboardStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total links
        long totalLinks = linkRepository.countLinksByUserId(userId);
        stats.put("totalLinks", totalLinks);
        
        // Total clicks
        Long totalClicks = clickRepository.countClicksByUserId(userId);
        stats.put("totalClicks", totalClicks != null ? totalClicks : 0L);
        
        // Recent activity (last 30 days)
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> dailyClicks = clickRepository.getDailyClicksForUser(userId, since);
        stats.put("dailyClicks", dailyClicks);
        
        // Top countries
        List<Object[]> topCountries = clickRepository.getClicksByCountryForUser(userId);
        stats.put("topCountries", topCountries.stream().limit(5).collect(Collectors.toList()));
        
        // Top performing links
        List<Link> topLinks = linkRepository.findByUserIdOrderByClickCountDesc(userId);
        stats.put("topLinks", topLinks.stream().limit(5).collect(Collectors.toList()));
        
        return stats;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs in X-Forwarded-For
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    private void parseUserAgent(Click click) {
        if (click.getUserAgent() != null) {
            try {
                UserAgent userAgent = UserAgent.parseUserAgentString(click.getUserAgent());
                click.setBrowser(userAgent.getBrowser().getName());
                click.setOperatingSystem(userAgent.getOperatingSystem().getName());
                click.setDeviceType(userAgent.getOperatingSystem().getDeviceType().getName());
                // Check if device type is mobile or tablet
                boolean isMobile = userAgent.getOperatingSystem().getDeviceType().equals(eu.bitwalker.useragentutils.DeviceType.MOBILE) || 
                                   userAgent.getOperatingSystem().getDeviceType().equals(eu.bitwalker.useragentutils.DeviceType.TABLET);
                click.setIsMobile(isMobile);
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Failed to parse user agent: " + e.getMessage());
            }
        }
    }
    
    private void getGeographicInfo(Click click) {
        if (click.getIpAddress() != null && !click.getIpAddress().isEmpty()) {
            try {
                if (geoIpReader == null && geoIpDatabasePath != null && !geoIpDatabasePath.isEmpty()) {
                    File database = new File(geoIpDatabasePath);
                    if (database.exists()) {
                        geoIpReader = new DatabaseReader.Builder(database).build();
                    }
                }
                
                if (geoIpReader != null) {
                    InetAddress ipAddress = InetAddress.getByName(click.getIpAddress());
                    CityResponse response = geoIpReader.city(ipAddress);
                    
                    if (response.getCountry() != null) {
                        click.setCountryCode(response.getCountry().getIsoCode());
                        click.setCountryName(response.getCountry().getName());
                    }
                    
                    if (response.getCity() != null) {
                        click.setCityName(response.getCity().getName());
                    }
                }
            } catch (IOException | GeoIp2Exception e) {
                // Log error but continue
                System.err.println("Failed to get geographic info: " + e.getMessage());
            }
        }
    }
    
    private void detectBot(Click click) {
        if (click.getUserAgent() != null) {
            String userAgent = click.getUserAgent().toLowerCase();
            String[] botKeywords = {
                "bot", "crawler", "spider", "scraper", "archiver", "indexer",
                "google", "bing", "yahoo", "facebook", "twitter", "linkedin",
                "whatsapp", "telegram", "slack", "discord"
            };
            
            for (String keyword : botKeywords) {
                if (userAgent.contains(keyword)) {
                    click.setIsBot(true);
                    return;
                }
            }
        }
        click.setIsBot(false);
    }
    
    private List<LinkAnalyticsResponse.DailyClickStats> getDailyClickStats(Long linkId, LocalDateTime since) {
        List<Object[]> rawData = clickRepository.getDailyClicksForLink(linkId, since);
        return rawData.stream()
                .map(row -> new LinkAnalyticsResponse.DailyClickStats(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.HourlyClickStats> getHourlyClickStats(List<Click> clicks) {
        Map<Integer, Long> hourlyStats = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyStats.put(i, 0L);
        }
        
        clicks.forEach(click -> {
            int hour = click.getClickedAt().getHour();
            hourlyStats.put(hour, hourlyStats.get(hour) + 1);
        });
        
        return hourlyStats.entrySet().stream()
                .map(entry -> new LinkAnalyticsResponse.HourlyClickStats(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(LinkAnalyticsResponse.HourlyClickStats::getHour))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.CountryClickStats> getCountryStats(List<Click> clicks) {
        Map<String, Long> countryStats = clicks.stream()
                .filter(click -> click.getCountryCode() != null)
                .collect(Collectors.groupingBy(Click::getCountryCode, Collectors.counting()));
        
        return countryStats.entrySet().stream()
                .map(entry -> {
                    Click sampleClick = clicks.stream()
                            .filter(c -> entry.getKey().equals(c.getCountryCode()))
                            .findFirst()
                            .orElse(null);
                    String countryName = sampleClick != null ? sampleClick.getCountryName() : entry.getKey();
                    return new LinkAnalyticsResponse.CountryClickStats(entry.getKey(), countryName, entry.getValue());
                })
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.CityClickStats> getCityStats(List<Click> clicks) {
        Map<String, Long> cityStats = clicks.stream()
                .filter(click -> click.getCityName() != null)
                .collect(Collectors.groupingBy(Click::getCityName, Collectors.counting()));
        
        return cityStats.entrySet().stream()
                .map(entry -> {
                    Click sampleClick = clicks.stream()
                            .filter(c -> entry.getKey().equals(c.getCityName()))
                            .findFirst()
                            .orElse(null);
                    String countryCode = sampleClick != null ? sampleClick.getCountryCode() : "Unknown";
                    return new LinkAnalyticsResponse.CityClickStats(entry.getKey(), countryCode, entry.getValue());
                })
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.BrowserStats> getBrowserStats(List<Click> clicks) {
        Map<String, Long> browserStats = clicks.stream()
                .filter(click -> click.getBrowser() != null)
                .collect(Collectors.groupingBy(Click::getBrowser, Collectors.counting()));
        
        return browserStats.entrySet().stream()
                .map(entry -> new LinkAnalyticsResponse.BrowserStats(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.OSStats> getOSStats(List<Click> clicks) {
        Map<String, Long> osStats = clicks.stream()
                .filter(click -> click.getOperatingSystem() != null)
                .collect(Collectors.groupingBy(Click::getOperatingSystem, Collectors.counting()));
        
        return osStats.entrySet().stream()
                .map(entry -> new LinkAnalyticsResponse.OSStats(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.DeviceStats> getDeviceStats(List<Click> clicks) {
        Map<String, Long> deviceStats = clicks.stream()
                .filter(click -> click.getDeviceType() != null)
                .collect(Collectors.groupingBy(Click::getDeviceType, Collectors.counting()));
        
        return deviceStats.entrySet().stream()
                .map(entry -> new LinkAnalyticsResponse.DeviceStats(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .collect(Collectors.toList());
    }
    
    private List<LinkAnalyticsResponse.ReferrerStats> getReferrerStats(List<Click> clicks) {
        Map<String, Long> referrerStats = clicks.stream()
                .filter(click -> click.getReferer() != null)
                .collect(Collectors.groupingBy(Click::getReferer, Collectors.counting()));
        
        return referrerStats.entrySet().stream()
                .map(entry -> new LinkAnalyticsResponse.ReferrerStats(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getClicks().compareTo(a.getClicks()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private Map<String, Long> getDeviceTypeBreakdown(List<Click> clicks) {
        Map<String, Long> breakdown = new HashMap<>();
        breakdown.put("mobile", clicks.stream().mapToLong(click -> click.getIsMobile() ? 1 : 0).sum());
        breakdown.put("desktop", clicks.size() - breakdown.get("mobile"));
        return breakdown;
    }
}
