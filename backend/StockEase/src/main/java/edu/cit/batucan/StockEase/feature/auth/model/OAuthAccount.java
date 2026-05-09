package edu.cit.batucan.StockEase.feature.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false)
    private String provider;
    
    @Column(nullable = false)
    private String providerUserId;
    
    @Column
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String accessToken;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
