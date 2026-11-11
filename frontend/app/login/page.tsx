import { signIn } from "@/auth";
import { Button } from "@/components/ui/button";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-6 p-8">
      <h1 className="text-2xl font-semibold tracking-tight">
        Sign in to AuditPilot
      </h1>
      <form
        action={async () => {
          "use server";
          await signIn("keycloak", { redirectTo: "/dashboard" });
        }}
      >
        <Button type="submit">Continue with Keycloak</Button>
      </form>
    </main>
  );
}