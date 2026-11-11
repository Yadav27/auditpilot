package com.auditpilot.backend.tenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantFilter extends OncePerRequestFilter {

    private final TenantRepository tenants;

    public TenantFilter(TenantRepository tenants) {
        this.tenants = tenants;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            chain.doFilter(request, response); // public endpoint, nothing to bind
            return;
        }

        List<String> slugs = TenantClaims.tenantSlugs(jwtAuth.getToken());
        if (slugs.size() != 1) {
            deny(response, "tenant_required");
            return;
        }

        Optional<Tenant> tenant = tenants.findBySlugAndStatus(slugs.get(0), TenantStatus.ACTIVE);
        if (tenant.isEmpty()) {
            deny(response, "tenant_unavailable");
            return;
        }

        Tenant t = tenant.get();
        TenantContext.set(new TenantRef(t.getId(), t.getSlug(), t.getName()));
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void deny(HttpServletResponse response, String code) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + code + "\"}");
    }
}