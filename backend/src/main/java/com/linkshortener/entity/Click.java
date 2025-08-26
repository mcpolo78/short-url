package com.linkshortener.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "clicks")
@EntityListeners(AuditingEntityListener.class)
public class Click {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id", nullable = false)
    @JsonBackReference
    private Link link;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    private String referer;
    
    @Column(name = "country_code", length = 2)
    private String countryCode;
    
    @Column(name = "country_name")
    private String countryName;
    
    @Column(name = "city_name")
    private String cityName;
    
    private String browser;
    
    @Column(name = "operating_system")
    private String operatingSystem;
    
    @Column(name = "device_type")
    private String deviceType;
    
    @Column(name = "is_mobile")
    private Boolean isMobile = false;
    
    @Column(name = "is_bot")
    private Boolean isBot = false;
    
    @CreatedDate
    @Column(name = "clicked_at", updatable = false)
    private LocalDateTime clickedAt;
    
    // Constructors
    public Click() {}
    
    public Click(Link link, String ipAddress, String userAgent) {
        this.link = link;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Link getLink() { return link; }
    public void setLink(Link link) { this.link = link; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getReferer() { return referer; }
    public void setReferer(String referer) { this.referer = referer; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    
    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public Boolean getIsMobile() { return isMobile; }
    public void setIsMobile(Boolean isMobile) { this.isMobile = isMobile; }
    
    public Boolean getIsBot() { return isBot; }
    public void setIsBot(Boolean isBot) { this.isBot = isBot; }
    
    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
}
