package com.prueba.graftsql.credito.auth.adapter;

import com.prueba.graftsql.credito.auth.application.ports.SocialAuthPort;
import com.prueba.graftsql.credito.auth.domain.AuthProvider;
import com.prueba.graftsql.credito.auth.domain.SocialUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GoogleOAuth2Adapter implements SocialAuthPort {
    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Override
    public String getAuthorizationUrl() {
        return String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&scope=email profile",
                GOOGLE_AUTH_URL, clientId, redirectUri
        );
    }

    @Override
    public SocialUserInfo exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String tokenRequest = String.format(
                    "grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s",
                    code, clientId, clientSecret, redirectUri
            );

            String tokenResponse = restTemplate.postForObject(
                    GOOGLE_TOKEN_URL, tokenRequest, String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenNode = mapper.readTree(tokenResponse);
            String accessToken = tokenNode.get("access_token").asText();

            JsonNode userInfoNode = mapper.readTree(
                    restTemplate.getForObject(
                            GOOGLE_USER_INFO_URL + "?access_token=" + accessToken,
                            String.class
                    )
            );

            SocialUserInfo userInfo = new SocialUserInfo(
                    userInfoNode.get("id").asText(),
                    userInfoNode.get("email").asText(),
                    userInfoNode.get("name").asText()
            );
            userInfo.setAccessToken(accessToken);

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Error exchanging Google code for token", e);
        }
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.GOOGLE;
    }
}
