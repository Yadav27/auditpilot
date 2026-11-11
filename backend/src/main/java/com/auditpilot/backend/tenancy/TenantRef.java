package com.auditpilot.backend.tenancy;

import java.util.UUID;

public record TenantRef(UUID id, String slug, String name) {}