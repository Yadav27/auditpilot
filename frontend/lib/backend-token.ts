import type { JWT } from "next-auth/jwt";

type Cached = { accessToken: string; expiresAt: number };

const cache = new Map<string, Cached>();
const SKEW_MS = 15_000;

/**
 * Returns a valid access token for the backend, refreshing it with
 * Keycloak when the one in the session cookie has expired.
 * Returns null if the user must sign in again.
 */
export async function getAccessToken(token: JWT): Promise<string | null> {
  const now = Date.now();

  if (
    typeof token.accessToken === "string" &&
    typeof token.expiresAt === "number" &&
    token.expiresAt * 1000 - SKEW_MS > now
  ) {
    return token.accessToken;
  }

  const key = token.sub ?? "";
  const cached = cache.get(key);
  if (cached && cached.expiresAt - SKEW_MS > now) return cached.accessToken;

  if (typeof token.refreshToken !== "string") return null;

  const res = await fetch(
    `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/token`,
    {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({
        grant_type: "refresh_token",
        client_id: process.env.AUTH_KEYCLOAK_ID!,
        client_secret: process.env.AUTH_KEYCLOAK_SECRET!,
        refresh_token: token.refreshToken,
      }),
    },
  );

  if (!res.ok) {
    cache.delete(key);
    return null;
  }

  const data = (await res.json()) as { access_token: string; expires_in: number };
  cache.set(key, {
    accessToken: data.access_token,
    expiresAt: now + data.expires_in * 1000,
  });
  return data.access_token;
}