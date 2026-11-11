# 0001: Tenant identity comes from Keycloak groups

Status: accepted

Context: Each request must map to one tenant. User attributes are silently
dropped by Keycloak unless declared in the user profile; groups import cleanly
and are admin-managed only.

Decision: Tenants are Keycloak groups named tenant-<slug>. A mapper exposes them
as the "groups" claim. The backend requires exactly one, looks it up in the
tenant table, and requires status ACTIVE.

Consequences: Suspending a tenant takes effect on the next request. Slugs are
immutable. Users in multiple tenants are not supported yet.