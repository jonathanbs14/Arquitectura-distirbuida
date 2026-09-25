package com.prueba.graftsql.credito.auth.web;

import java.util.List;
import java.util.Map;

import com.prueba.graftsql.credito.auth.adapter.MFAVerifyResponse;
import com.prueba.graftsql.credito.auth.adapter.OAuthCallbackRequest;
import com.prueba.graftsql.credito.auth.adapter.OAuthUrlResponse;
import com.prueba.graftsql.credito.auth.adapter.RegisterRequest;
import com.prueba.graftsql.credito.auth.application.AuthResponse;
import com.prueba.graftsql.credito.auth.application.LoginRequest;
import com.prueba.graftsql.credito.auth.application.MFASetupResponse;
import com.prueba.graftsql.credito.auth.application.MFAVerifyRequest;
import com.prueba.graftsql.credito.auth.application.ports.*;
import com.prueba.graftsql.credito.auth.application.usercase.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Límite de identidad del sistema. No acepta tokens sin verificarlos contra un proveedor OIDC.
 * Las URL y secretos del proveedor se inyectan al desplegar y nunca se guardan en este repositorio.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterLocalUseCase registerLocalUseCase;
    private final LoginUseCase loginUseCase;
    private final RegisterOAuthUseCase registerOAuthUseCase;
    private final SetupMFAUseCase setupMFAUseCase;
    private final VerifyMFAUseCase verifyMFAUseCase;
    private final List<SocialAuthPort> socialAuthPorts;

    public AuthController(RegisterLocalUseCase registerLocalUseCase, LoginUseCase loginUseCase,
                          RegisterOAuthUseCase registerOAuthUseCase, SetupMFAUseCase setupMFAUseCase,
                          VerifyMFAUseCase verifyMFAUseCase, List<SocialAuthPort> socialAuthPorts) {
        this.registerLocalUseCase = registerLocalUseCase;
        this.loginUseCase = loginUseCase;
        this.registerOAuthUseCase = registerOAuthUseCase;
        this.setupMFAUseCase = setupMFAUseCase;
        this.verifyMFAUseCase = verifyMFAUseCase;
        this.socialAuthPorts = socialAuthPorts;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return registerLocalUseCase.execute(request.getEmail(), request.getFullName(), request.getPassword());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return loginUseCase.execute(request);
    }

    @GetMapping("/oauth/{provider}")
    public OAuthUrlResponse getOAuthUrl(@PathVariable String provider) {
        SocialAuthPort authPort = socialAuthPorts.stream()
                .filter(port -> port.getProvider().name().equalsIgnoreCase(provider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        return new OAuthUrlResponse(authPort.getAuthorizationUrl());
    }

    @PostMapping("/oauth/{provider}/callback")
    public AuthResponse oauthCallback(@PathVariable String provider, @RequestBody OAuthCallbackRequest request) {
        SocialAuthPort authPort = socialAuthPorts.stream()
                .filter(port -> port.getProvider().name().equalsIgnoreCase(provider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        var userInfo = authPort.exchangeCodeForToken(request.getCode());
        return registerOAuthUseCase.execute(userInfo, authPort.getProvider());
    }

    @PostMapping("/mfa/setup/{userId}")
    public MFASetupResponse setupMFA(@PathVariable String userId) {
        return setupMFAUseCase.execute(userId);
    }

    @PostMapping("/mfa/verify")
    public MFAVerifyResponse verifyMFA(@RequestBody MFAVerifyRequest request) {
        boolean isValid = verifyMFAUseCase.execute(request);
        return new MFAVerifyResponse(isValid);
    }
}
