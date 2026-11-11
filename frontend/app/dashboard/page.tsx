import { redirect } from "next/navigation";
import { auth, signOut } from "@/auth";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { WhoAmI } from "@/components/who-am-i";

const APP_ROLES = [
  "PLATFORM_ADMIN",
  "TENANT_ADMIN",
  "COMPLIANCE_OFFICER",
  "ENGINEER",
  "AUDITOR",
];

export default async function DashboardPage() {
  const session = await auth();
  if (!session?.user) redirect("/login");

  const roles = (session.roles ?? []).filter((r) => APP_ROLES.includes(r));

  return (
    <main className="mx-auto flex min-h-screen max-w-2xl flex-col gap-6 p-8">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">Dashboard</h1>
        <form
          action={async () => {
            "use server";
            await signOut({ redirectTo: "/" });
          }}
        >
          <Button variant="outline" type="submit">
            Sign out
          </Button>
        </form>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>{session.user.name ?? session.user.email}</CardTitle>
          <CardDescription>
            Roles shown here are a UI hint only. The backend enforces access.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-2">
          {roles.length === 0 && <Badge variant="secondary">No roles</Badge>}
          {roles.map((r) => (
            <Badge key={r}>{r}</Badge>
          ))}
        </CardContent>
      </Card>

      <WhoAmI />
    </main>
  );
}