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
public class GitHubOAuth2Adapter implements SocialAuthPort {
    @Value("${oauth.github.client-id}")
    private String clientId;

    @Value("${oauth.github.client-secret}")
    private String clientSecret;

    @Value("${oauth.github.redirect-uri}")
    private String redirectUri;

    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_INFO_URL = "https://api.github.com/user";

    @Override
    public String getAuthorizationUrl() {
        return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=user:email",
                GITHUB_AUTH_URL, clientId, redirectUri
        );
    }

    @Override
    public SocialUserInfo exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String tokenRequest = String.format(
                    "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"code\":\"%s\"}",
                    clientId, clientSecret, code
            );

            String tokenResponse = restTemplate.postForObject(
                    GITHUB_TOKEN_URL, tokenRequest, String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenNode = mapper.readTree(tokenResponse);
            String accessToken = tokenNode.get("access_token").asText();

            JsonNode userInfoNode = mapper.readTree(
                    restTemplate.getForObject(
                            GITHUB_USER_INFO_URL,
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
            throw new RuntimeException("Error exchanging GitHub code for token", e);
        }
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.GITHUB;
    }
}
