package com.auditpilot.backend.tenancy;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TenantClaims {

    public static final String GROUP_PREFIX = "tenant-";

    private TenantClaims() {}

    /** Returns tenant slugs found in the token's "groups" claim. */
    public static List<String> tenantSlugs(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList("groups");
        if (groups == null) {
            return List.of();
        }
        return groups.stream()
                .filter(g -> g.startsWith(GROUP_PREFIX) && g.length() > GROUP_PREFIX.length())
                .map(g -> g.substring(GROUP_PREFIX.length()))
                .distinct()
                .toList();
    }
}