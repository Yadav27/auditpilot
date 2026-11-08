import { BackendStatus } from "@/components/backend-status";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-6 p-8">
      <h1 className="text-3xl font-semibold tracking-tight">AuditPilot</h1>
      <p className="text-muted-foreground">
        Continuous compliance for Canadian software teams.
      </p>
      <BackendStatus />
    </main>
  );
}