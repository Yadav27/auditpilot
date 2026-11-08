package com.auditpilot.backend.api;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return Map.of(
            "username", jwt.getClaimAsString("preferred_username"),
            "roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList());
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public Map<String, String> adminPing() {
        return Map.of("status", "ok", "scope", "tenant-admin");
    }
}