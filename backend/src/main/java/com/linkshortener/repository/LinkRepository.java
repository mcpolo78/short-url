package com.linkshortener.repository;

import com.linkshortener.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    
    Optional<Link> findByShortCode(String shortCode);
    
    Optional<Link> findByCustomAlias(String customAlias);
    
    @Query("SELECT l FROM Link l WHERE l.shortCode = ?1 OR l.customAlias = ?1")
    Optional<Link> findByShortCodeOrCustomAlias(String code);
    
    boolean existsByShortCode(String shortCode);
    
    boolean existsByCustomAlias(String customAlias);
    
    Page<Link> findByUserId(Long userId, Pageable pageable);
    
    Page<Link> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT l FROM Link l WHERE l.user.id = ?1 ORDER BY l.createdAt DESC")
    List<Link> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT l FROM Link l WHERE l.user.id = ?1 ORDER BY l.clickCount DESC")
    List<Link> findByUserIdOrderByClickCountDesc(Long userId);
    
    @Query("SELECT l FROM Link l WHERE l.isActive = true AND (l.expiresAt IS NULL OR l.expiresAt > ?1)")
    List<Link> findActiveLinks(LocalDateTime currentTime);
    
    @Query("SELECT l FROM Link l WHERE l.expiresAt IS NOT NULL AND l.expiresAt <= ?1 AND l.isActive = true")
    List<Link> findExpiredLinks(LocalDateTime currentTime);
    
    @Query("SELECT COUNT(l) FROM Link l WHERE l.createdAt >= ?1 AND l.createdAt < ?2")
    long countLinksCreatedBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT l FROM Link l WHERE l.originalUrl LIKE %?1% OR l.title LIKE %?1% OR l.description LIKE %?1%")
    List<Link> searchLinks(String keyword);
    
    @Query("SELECT l FROM Link l WHERE l.user.id = ?1 AND (l.originalUrl LIKE %?2% OR l.title LIKE %?2% OR l.description LIKE %?2%)")
    List<Link> searchLinksByUser(Long userId, String keyword);
    
    @Query("SELECT l FROM Link l ORDER BY l.clickCount DESC")
    List<Link> findTopLinks(Pageable pageable);
    
    @Query("SELECT l FROM Link l WHERE l.user.id = ?1 AND l.createdAt >= ?2")
    List<Link> findRecentLinksByUser(Long userId, LocalDateTime since);
    
    @Query("SELECT COUNT(l) FROM Link l WHERE l.user.id = ?1")
    long countLinksByUserId(Long userId);
}
