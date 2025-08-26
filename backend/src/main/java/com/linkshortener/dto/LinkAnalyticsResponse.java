package com.linkshortener.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LinkAnalyticsResponse {
    
    private Long linkId;
    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private Long uniqueClicks;
    private LocalDateTime createdAt;
    private LocalDateTime lastClickAt;
    
    // Click analytics by time
    private List<DailyClickStats> dailyClicks;
    private List<HourlyClickStats> hourlyClicks;
    
    // Geographic analytics
    private List<CountryClickStats> clicksByCountry;
    private List<CityClickStats> clicksByCity;
    
    // Technology analytics
    private List<BrowserStats> clicksByBrowser;
    private List<OSStats> clicksByOS;
    private List<DeviceStats> clicksByDevice;
    
    // Referrer analytics
    private List<ReferrerStats> clicksByReferrer;
    
    // Mobile/Desktop breakdown
    private Map<String, Long> deviceTypeBreakdown;
    
    // Bot detection
    private Long botClicks;
    private Long realClicks;
    
    // Constructors
    public LinkAnalyticsResponse() {}
    
    // Inner classes for statistics
    public static class DailyClickStats {
        private String date;
        private Long clicks;
        
        public DailyClickStats() {}
        public DailyClickStats(String date, Long clicks) {
            this.date = date;
            this.clicks = clicks;
        }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class HourlyClickStats {
        private Integer hour;
        private Long clicks;
        
        public HourlyClickStats() {}
        public HourlyClickStats(Integer hour, Long clicks) {
            this.hour = hour;
            this.clicks = clicks;
        }
        
        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class CountryClickStats {
        private String countryCode;
        private String countryName;
        private Long clicks;
        
        public CountryClickStats() {}
        public CountryClickStats(String countryCode, String countryName, Long clicks) {
            this.countryCode = countryCode;
            this.countryName = countryName;
            this.clicks = clicks;
        }
        
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public String getCountryName() { return countryName; }
        public void setCountryName(String countryName) { this.countryName = countryName; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class CityClickStats {
        private String cityName;
        private String countryCode;
        private Long clicks;
        
        public CityClickStats() {}
        public CityClickStats(String cityName, String countryCode, Long clicks) {
            this.cityName = cityName;
            this.countryCode = countryCode;
            this.clicks = clicks;
        }
        
        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class BrowserStats {
        private String browser;
        private Long clicks;
        
        public BrowserStats() {}
        public BrowserStats(String browser, Long clicks) {
            this.browser = browser;
            this.clicks = clicks;
        }
        
        public String getBrowser() { return browser; }
        public void setBrowser(String browser) { this.browser = browser; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class OSStats {
        private String operatingSystem;
        private Long clicks;
        
        public OSStats() {}
        public OSStats(String operatingSystem, Long clicks) {
            this.operatingSystem = operatingSystem;
            this.clicks = clicks;
        }
        
        public String getOperatingSystem() { return operatingSystem; }
        public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class DeviceStats {
        private String deviceType;
        private Long clicks;
        
        public DeviceStats() {}
        public DeviceStats(String deviceType, Long clicks) {
            this.deviceType = deviceType;
            this.clicks = clicks;
        }
        
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    public static class ReferrerStats {
        private String referrer;
        private Long clicks;
        
        public ReferrerStats() {}
        public ReferrerStats(String referrer, Long clicks) {
            this.referrer = referrer;
            this.clicks = clicks;
        }
        
        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
        public Long getClicks() { return clicks; }
        public void setClicks(Long clicks) { this.clicks = clicks; }
    }
    
    // Main class getters and setters
    public Long getLinkId() { return linkId; }
    public void setLinkId(Long linkId) { this.linkId = linkId; }
    
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    
    public Long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(Long totalClicks) { this.totalClicks = totalClicks; }
    
    public Long getUniqueClicks() { return uniqueClicks; }
    public void setUniqueClicks(Long uniqueClicks) { this.uniqueClicks = uniqueClicks; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastClickAt() { return lastClickAt; }
    public void setLastClickAt(LocalDateTime lastClickAt) { this.lastClickAt = lastClickAt; }
    
    public List<DailyClickStats> getDailyClicks() { return dailyClicks; }
    public void setDailyClicks(List<DailyClickStats> dailyClicks) { this.dailyClicks = dailyClicks; }
    
    public List<HourlyClickStats> getHourlyClicks() { return hourlyClicks; }
    public void setHourlyClicks(List<HourlyClickStats> hourlyClicks) { this.hourlyClicks = hourlyClicks; }
    
    public List<CountryClickStats> getClicksByCountry() { return clicksByCountry; }
    public void setClicksByCountry(List<CountryClickStats> clicksByCountry) { this.clicksByCountry = clicksByCountry; }
    
    public List<CityClickStats> getClicksByCity() { return clicksByCity; }
    public void setClicksByCity(List<CityClickStats> clicksByCity) { this.clicksByCity = clicksByCity; }
    
    public List<BrowserStats> getClicksByBrowser() { return clicksByBrowser; }
    public void setClicksByBrowser(List<BrowserStats> clicksByBrowser) { this.clicksByBrowser = clicksByBrowser; }
    
    public List<OSStats> getClicksByOS() { return clicksByOS; }
    public void setClicksByOS(List<OSStats> clicksByOS) { this.clicksByOS = clicksByOS; }
    
    public List<DeviceStats> getClicksByDevice() { return clicksByDevice; }
    public void setClicksByDevice(List<DeviceStats> clicksByDevice) { this.clicksByDevice = clicksByDevice; }
    
    public List<ReferrerStats> getClicksByReferrer() { return clicksByReferrer; }
    public void setClicksByReferrer(List<ReferrerStats> clicksByReferrer) { this.clicksByReferrer = clicksByReferrer; }
    
    public Map<String, Long> getDeviceTypeBreakdown() { return deviceTypeBreakdown; }
    public void setDeviceTypeBreakdown(Map<String, Long> deviceTypeBreakdown) { this.deviceTypeBreakdown = deviceTypeBreakdown; }
    
    public Long getBotClicks() { return botClicks; }
    public void setBotClicks(Long botClicks) { this.botClicks = botClicks; }
    
    public Long getRealClicks() { return realClicks; }
    public void setRealClicks(Long realClicks) { this.realClicks = realClicks; }
}
