package com.prueba.graftsql.credito.auth.adapter;

public class OAuthCallbackRequest {
    private String code;

    public OAuthCallbackRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
