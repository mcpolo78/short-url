package com.linkshortener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://shortline.proxy.rlwy.net:26485/railway";
        String username = "root";
        String password = "SDjnYiqCCMFQWlOWeWnhIDFqIaCRTVsr";
        
        String[] tables = {
            // Table users
            """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_username (username),
                INDEX idx_email (email)
            )
            """,
            
            // Table links
            """
            CREATE TABLE IF NOT EXISTS links (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                short_code VARCHAR(10) NOT NULL UNIQUE,
                original_url TEXT NOT NULL,
                title VARCHAR(255),
                description TEXT,
                user_id BIGINT,
                click_count BIGINT DEFAULT 0,
                is_active BOOLEAN DEFAULT TRUE,
                expires_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                INDEX idx_short_code (short_code),
                INDEX idx_user_id (user_id),
                INDEX idx_created_at (created_at),
                INDEX idx_active (is_active)
            )
            """,
            
            // Table qr_codes
            """
            CREATE TABLE IF NOT EXISTS qr_codes (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                link_id BIGINT NOT NULL,
                file_path VARCHAR(255) NOT NULL,
                size INTEGER DEFAULT 200,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (link_id) REFERENCES links(id) ON DELETE CASCADE,
                INDEX idx_link_id (link_id)
            )
            """,
            
            // Utilisateur de test
            """
            INSERT IGNORE INTO users (username, password, email) 
            VALUES ('testuser', '$2a$10$DowqY8ELlW8.xH2qBB0EguZp8RzJW2aH7P8PxYEyj8d4K9RlFa8Ey', 'test@example.com')
            """
        };
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("✅ Connexion MySQL Railway réussie !");
            
            for (int i = 0; i < tables.length; i++) {
                stmt.executeUpdate(tables[i]);
                System.out.println("✅ Requête " + (i+1) + " exécutée avec succès");
            }
            
            System.out.println("🎯 Toutes les tables ont été créées !");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création des tables : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
