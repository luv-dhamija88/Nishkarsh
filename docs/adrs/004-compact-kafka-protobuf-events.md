## ADR 004: Compact Protobuf Kafka Events for Raw Telemetry

Date: 2026-05-27

### Context

`ingestion-service` now receives full OTLP trace and log payloads.

Those payloads are rich, nested, and useful for normalization, but they are larger than what the immediate downstream processing slice needs. The next service, `incident-processor`, is not yet consuming the full telemetry model and currently only needs a compact subset of fields for grouping, correlation, and incident creation.

The system also needs an event format that is:

- compact over Kafka
- language-neutral across future Java and Python consumers
- explicit enough to version cleanly
- efficient to serialize and compress

### Decision

Use compact protobuf messages serialized to raw `byte[]` for the first raw Kafka topics emitted by `ingestion-service`.

Current topics:

- `raw-logs`
- `raw-spans`

Current producer choices:

- value serializer: `ByteArraySerializer`
- key serializer: `StringSerializer`
- compression: `lz4`
- partition key: service name

Current contract scope:

- `raw-logs` carries a compact log event subset
- `raw-spans` carries a compact span event subset
- only the fields needed by the next incident-processing slice are forwarded today

### Rationale

Why protobuf:

- smaller wire format than JSON for structured events
- strong schema evolution story
- good interoperability for future Java/Python consumers
- works cleanly with Kafka `byte[]` publishing

Why compact events instead of full OTLP payload forwarding:

- reduces Kafka payload size
- keeps the downstream contract focused on incident-processing needs
- avoids coupling `incident-processor` to the full OTLP schema too early
- makes the vertical slice easier to explain and test

Why `lz4` compression:

- improves payload size further while keeping producer configuration simple
- works natively with Kafka clients already in use in the project

### Consequences

Positive:

- lower payload overhead on Kafka
- explicit and testable raw-topic schemas
- easier future extraction into shared contracts
- clean boundary between OTLP ingestion concerns and incident-processing concerns

Trade-offs:

- current raw-topic contracts live inside `ingestion-service` instead of `packages/contracts`
- not all OTLP attributes are forwarded today
- schema evolution discipline is required once consumers begin relying on these topics

### Guardrails

- Keep raw topic messages intentionally small and versionable.
- Do not promote generated protobuf Java code to handwritten source ownership.
- Move the stable `.proto` definitions to `packages/contracts` before multiple services depend on them.
- Add fields only when justified by measured downstream need.

