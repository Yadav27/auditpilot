package com.auditpilot.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import com.auditpilot.backend.tenancy.*;

class TenantFilterTest {

    private final TenantRepository repo = mock(TenantRepository.class);
    private final TenantFilter filter = new TenantFilter(repo);

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    private void authenticateWithGroups(List<String> groups) {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").claim("groups", groups).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @Test
    void bindsTenantDuringRequestAndClearsAfterwards() throws Exception {
        UUID id = UUID.randomUUID();
        when(repo.findBySlugAndStatus("acme-bank", TenantStatus.ACTIVE))
                .thenReturn(Optional.of(new Tenant(id, "acme-bank", "Acme Bank", TenantStatus.ACTIVE)));
        authenticateWithGroups(List.of("tenant-acme-bank"));

        AtomicReference<TenantRef> seen = new AtomicReference<>();
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response,
                (req, res) -> seen.set(TenantContext.current()));

        assertEquals(id, seen.get().id());
        assertNull(TenantContext.current());
        assertEquals(200, response.getStatus());
    }

    @Test
    void rejectsUserWithoutTenantGroup() throws Exception {
        authenticateWithGroups(List.of("staff"));
        var response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());

        assertEquals(403, response.getStatus());
    }

    @Test
    void rejectsUserInTwoTenants() throws Exception {
        authenticateWithGroups(List.of("tenant-acme-bank", "tenant-birch-insurance"));
        var response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());

        assertEquals(403, response.getStatus());
    }

    @Test
    void rejectsSuspendedOrUnknownTenant() throws Exception {
        when(repo.findBySlugAndStatus("acme-bank", TenantStatus.ACTIVE)).thenReturn(Optional.empty());
        authenticateWithGroups(List.of("tenant-acme-bank"));
        var response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());

        assertEquals(403, response.getStatus());
    }
}
