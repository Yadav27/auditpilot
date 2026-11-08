"use client";

import { useEffect, useState } from "react";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

type PingResponse = { status: string; service: string };

type State =
  | { kind: "loading" }
  | { kind: "ok"; data: PingResponse }
  | { kind: "error"; message: string };

export function BackendStatus() {
  const [state, setState] = useState<State>({ kind: "loading" });

  useEffect(() => {
    const controller = new AbortController();

    fetch("/api/v1/ping", { signal: controller.signal })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json() as Promise<PingResponse>;
      })
      .then((data) => setState({ kind: "ok", data }))
      .catch((err: unknown) => {
        if (controller.signal.aborted) return;
        setState({
          kind: "error",
          message: err instanceof Error ? err.message : "Unknown error",
        });
      });

    return () => controller.abort();
  }, []);

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>Backend status</CardTitle>
        <CardDescription>
          Calls /api/v1/ping through the Next.js proxy
        </CardDescription>
      </CardHeader>
      <CardContent>
        {state.kind === "loading" && <Badge variant="secondary">Checking…</Badge>}
        {state.kind === "ok" && (
          <div className="flex items-center gap-2">
            <Badge>Connected</Badge>
            <span className="text-sm text-muted-foreground">
              {state.data.service}
            </span>
          </div>
        )}
        {state.kind === "error" && (
          <Badge variant="destructive">Unreachable: {state.message}</Badge>
        )}
      </CardContent>
    </Card>
  );
}