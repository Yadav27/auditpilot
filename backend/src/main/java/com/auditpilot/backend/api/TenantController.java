package com.auditpilot.backend.api;

import com.auditpilot.backend.tenancy.TenantContext;
import com.auditpilot.backend.tenancy.TenantRef;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TenantController {

    @GetMapping("/tenant")
    public TenantRef currentTenant() {
        return TenantContext.require();
    }
}