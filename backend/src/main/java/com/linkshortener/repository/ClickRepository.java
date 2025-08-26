package com.linkshortener.repository;

import com.linkshortener.entity.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {
    
    List<Click> findByLinkId(Long linkId);
    
    List<Click> findByLinkIdOrderByClickedAtDesc(Long linkId);
    
    @Query("SELECT COUNT(c) FROM Click c WHERE c.link.id = ?1")
    long countClicksByLinkId(Long linkId);
    
    @Query("SELECT COUNT(c) FROM Click c WHERE c.link.id = ?1 AND c.clickedAt >= ?2 AND c.clickedAt < ?3")
    long countClicksByLinkIdAndDateRange(Long linkId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c.countryCode, COUNT(c) FROM Click c WHERE c.link.id = ?1 GROUP BY c.countryCode ORDER BY COUNT(c) DESC")
    List<Object[]> getClicksByCountryForLink(Long linkId);
    
    @Query("SELECT c.browser, COUNT(c) FROM Click c WHERE c.link.id = ?1 GROUP BY c.browser ORDER BY COUNT(c) DESC")
    List<Object[]> getClicksByBrowserForLink(Long linkId);
    
    @Query("SELECT c.operatingSystem, COUNT(c) FROM Click c WHERE c.link.id = ?1 GROUP BY c.operatingSystem ORDER BY COUNT(c) DESC")
    List<Object[]> getClicksByOSForLink(Long linkId);
    
    @Query("SELECT c.deviceType, COUNT(c) FROM Click c WHERE c.link.id = ?1 GROUP BY c.deviceType ORDER BY COUNT(c) DESC")
    List<Object[]> getClicksByDeviceForLink(Long linkId);
    
    @Query("SELECT DATE(c.clickedAt), COUNT(c) FROM Click c WHERE c.link.id = ?1 AND c.clickedAt >= ?2 GROUP BY DATE(c.clickedAt) ORDER BY DATE(c.clickedAt)")
    List<Object[]> getDailyClicksForLink(Long linkId, LocalDateTime since);
    
    @Query("SELECT c FROM Click c WHERE c.link.user.id = ?1")
    List<Click> findClicksByUserId(Long userId);
    
    @Query("SELECT COUNT(c) FROM Click c WHERE c.link.user.id = ?1")
    long countClicksByUserId(Long userId);
    
    @Query("SELECT c.countryCode, COUNT(c) FROM Click c WHERE c.link.user.id = ?1 GROUP BY c.countryCode ORDER BY COUNT(c) DESC")
    List<Object[]> getClicksByCountryForUser(Long userId);
    
    @Query("SELECT DATE(c.clickedAt), COUNT(c) FROM Click c WHERE c.link.user.id = ?1 AND c.clickedAt >= ?2 GROUP BY DATE(c.clickedAt) ORDER BY DATE(c.clickedAt)")
    List<Object[]> getDailyClicksForUser(Long userId, LocalDateTime since);
    
    @Query("SELECT COUNT(c) FROM Click c WHERE c.clickedAt >= ?1 AND c.clickedAt < ?2")
    long countClicksBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM Click c WHERE c.isBot = false ORDER BY c.clickedAt DESC")
    List<Click> findRecentRealClicks(org.springframework.data.domain.Pageable pageable);
}
