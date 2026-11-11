import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

declare module "next-auth" {
  interface Session {
    roles?: string[];
  }
}

// UI hint only: reads roles from the token without verifying it.
// The backend is the only place access is actually enforced.
function realmRoles(accessToken?: string | null): string[] {
  if (!accessToken) return [];
  try {
    const payload = JSON.parse(
      Buffer.from(accessToken.split(".")[1], "base64url").toString("utf8"),
    );
    const roles = payload?.realm_access?.roles;
    return Array.isArray(roles)
      ? roles.filter((r): r is string => typeof r === "string")
      : [];
  } catch {
    return [];
  }
}

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [Keycloak],
  session: { strategy: "jwt" },
  pages: { signIn: "/login" },
  callbacks: {
    authorized: ({ auth }) => !!auth,
    jwt({ token, account }) {
      if (account) {
        token.accessToken = account.access_token;
        token.refreshToken = account.refresh_token;
        token.expiresAt = account.expires_at;
        token.idToken = account.id_token;
        token.roles = realmRoles(account.access_token);
      }
      return token;
    },
    session({ session, token }) {
      // Deliberately NOT exposing any token to the browser.
      session.roles = Array.isArray(token.roles) ? (token.roles as string[]) : [];
      return session;
    },
  },
  events: {
    // Also end the Keycloak session, otherwise "Sign in" would log
    // the user straight back in without asking for a password.
    async signOut(message) {
      const idToken =
        "token" in message ? (message.token?.idToken as string | undefined) : undefined;
      if (!idToken) return;
      const url = new URL(
        `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/logout`,
      );
      url.searchParams.set("id_token_hint", idToken);
      await fetch(url).catch(() => undefined);
    },
  },
});