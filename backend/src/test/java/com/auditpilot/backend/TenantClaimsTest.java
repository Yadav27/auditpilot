package com.auditpilot.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import com.auditpilot.backend.tenancy.*;

class TenantClaimsTest {

    private Jwt jwtWithGroups(List<String> groups) {
        return Jwt.withTokenValue("t").header("alg", "none").claim("groups", groups).build();
    }

    @Test
    void extractsSlugFromTenantGroup() {
        var slugs = TenantClaims.tenantSlugs(jwtWithGroups(List.of("tenant-acme-bank", "other")));
        assertEquals(List.of("acme-bank"), slugs);
    }

    @Test
    void ignoresBarePrefixAndUnrelatedGroups() {
        assertTrue(TenantClaims.tenantSlugs(jwtWithGroups(List.of("tenant-", "staff"))).isEmpty());
    }

    @Test
    void returnsEmptyWhenClaimMissing() {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").claim("sub", "x").build();
        assertTrue(TenantClaims.tenantSlugs(jwt).isEmpty());
    }
}