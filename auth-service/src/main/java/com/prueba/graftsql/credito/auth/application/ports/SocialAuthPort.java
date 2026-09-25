package com.prueba.graftsql.credito.auth.application.ports;

import com.prueba.graftsql.credito.auth.domain.AuthProvider;
import com.prueba.graftsql.credito.auth.domain.SocialUserInfo;

public interface SocialAuthPort {
    String getAuthorizationUrl();
    SocialUserInfo exchangeCodeForToken(String code);
    AuthProvider getProvider();
}
