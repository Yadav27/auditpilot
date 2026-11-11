"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

type Result = { path: string; status: number | "error"; body?: unknown };

const PATHS = ["/api/v1/me", "/api/v1/admin/ping"];

export function WhoAmI() {
  const [results, setResults] = useState<Result[] | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    Promise.all(
      PATHS.map(async (path): Promise<Result> => {
        try {
          const res = await fetch(path, { signal: controller.signal });
          const body = res.ok ? await res.json() : undefined;
          return { path, status: res.status, body };
        } catch {
          return { path, status: "error" };
        }
      }),
    ).then((r) => {
      if (!controller.signal.aborted) setResults(r);
    });

    return () => controller.abort();
  }, []);

  const expired = results?.some((r) => r.status === 401);

  return (
    <Card>
      <CardHeader>
        <CardTitle>What the backend says</CardTitle>
        <CardDescription>
          Real answers from Spring Security through the server-side proxy
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {results === null && <Badge variant="secondary">Checking…</Badge>}
        {results?.map((r) => (
          <div key={r.path} className="flex flex-col gap-1">
            <div className="flex items-center gap-2">
              <code className="text-sm">{r.path}</code>
              <Badge variant={r.status === 200 ? "default" : "secondary"}>
                {r.status}
              </Badge>
            </div>
            {r.body !== undefined && (
              <pre className="rounded bg-muted p-2 text-xs">
                {JSON.stringify(r.body, null, 2)}
              </pre>
            )}
          </div>
        ))}
        {expired && (
          <p className="text-sm">
            Your session expired.{" "}
            <Link href="/login" className="underline">
              Sign in again
            </Link>
          </p>
        )}
      </CardContent>
    </Card>
  );
}