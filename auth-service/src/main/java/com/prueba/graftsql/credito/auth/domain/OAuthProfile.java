package com.prueba.graftsql.credito.auth.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "oauth_profiles")
public class OAuthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(unique = true, nullable = false)
    private String externalId;

    private String accessToken;
    private String refreshToken;

    public OAuthProfile() {}

    public OAuthProfile(String userId, AuthProvider provider, String externalId) {
        this.userId = userId;
        this.provider = provider;
        this.externalId = externalId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public AuthProvider getProvider() { return provider; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
