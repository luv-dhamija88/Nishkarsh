# ADR-004: Custom OTEL gRPC Collector in ingestion-service

- Date: 2026-05-25

## Context

`ingestion-service` needs to ingest OpenTelemetry trace data over gRPC so we can normalize runtime signals for downstream incident intelligence and AI RCA.

The earlier approach used a very large flat telemetry model that broke compilation because Lombok-generated constructor paths exceeded Java parameter limits.

## Decision

We implement a custom OTLP trace collector inside `ingestion-service` with these decisions:

1. Expose gRPC OTLP trace endpoint using `TraceServiceGrpc.TraceServiceImplBase`.
2. Parse `ExportTraceServiceRequest` and map semantic-convention attributes to internal models.
3. Use nested Lombok context models (`TelemetrySpan` with nested classes) instead of a giant flat class.
4. Keep every missing attribute as `null` (best-effort ingestion, no hard rejection for sparse payloads).
5. Process exports through reactive service methods (`Mono`/`Flux`) to keep ingestion non-blocking.

## Consequences

### Positive

- Build is stable again (no constructor parameter overflow).
- gRPC endpoint is OTLP-compatible at the trace service level.
- Attribute mapping is explicit and aligned with semantic-convention names.
- Sparse telemetry from clients is still accepted.

### Trade-offs

- Current scope is traces export handling; metrics/logs OTLP ingestion can be added later.
- Processing currently logs normalized spans; persistence/routing can be connected next.

