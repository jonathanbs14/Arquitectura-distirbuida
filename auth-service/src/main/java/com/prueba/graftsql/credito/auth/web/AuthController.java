package com.prueba.graftsql.credito.auth.web;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Límite de identidad del sistema. No acepta tokens sin verificarlos contra un proveedor OIDC.
 * Las URL y secretos del proveedor se inyectan al desplegar y nunca se guardan en este repositorio.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/providers")
    public Map<String, List<String>> providers() {
        return Map.of("providers", List.of("google", "facebook"));
    }

    @PostMapping("/login")
    public void login() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "Configure un adaptador OIDC y sus secretos mediante variables de entorno");
    }
}
