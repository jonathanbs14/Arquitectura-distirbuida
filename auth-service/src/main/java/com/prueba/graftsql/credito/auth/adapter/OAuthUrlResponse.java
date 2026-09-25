package com.prueba.graftsql.credito.auth.adapter;

public class OAuthUrlResponse {
    private String authorizationUrl;

    public OAuthUrlResponse(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getAuthorizationUrl() { return authorizationUrl; }
    public void setAuthorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; }
}
