package com.auditpilot.backend.tenancy;

public final class TenantContext {

    private static final ThreadLocal<TenantRef> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(TenantRef tenant) {
        CURRENT.set(tenant);
    }

    public static TenantRef require() {
        TenantRef tenant = CURRENT.get();
        if (tenant == null) {
            throw new IllegalStateException("No tenant bound to the current request");
        }
        return tenant;
    }

    public static TenantRef current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}