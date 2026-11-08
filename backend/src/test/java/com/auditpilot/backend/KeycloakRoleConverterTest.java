package com.auditpilot.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import com.auditpilot.backend.security.KeycloakRoleConverter;

class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter converter = new KeycloakRoleConverter();

    @Test
    void mapsRealmRolesWithRolePrefix() {
        Jwt jwt = Jwt.withTokenValue("t")
            .header("alg", "none")
            .claim("realm_access", Map.of("roles", List.of("ENGINEER", "offline_access")))
            .build();

        var names = converter.convert(jwt).stream().map(GrantedAuthority::getAuthority).toList();

        assertTrue(names.contains("ROLE_ENGINEER"));
        assertEquals(2, names.size());
    }

    @Test
    void returnsNoAuthoritiesWhenClaimMissing() {
        Jwt jwt = Jwt.withTokenValue("t")
            .header("alg", "none")
            .claim("sub", "someone")
            .build();

        assertTrue(converter.convert(jwt).isEmpty());
    }
}