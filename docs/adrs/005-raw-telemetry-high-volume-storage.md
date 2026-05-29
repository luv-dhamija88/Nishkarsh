## ADR 005: High-Volume Raw Telemetry Storage Strategy

Date: 2026-05-27

### Context

`ingestion-service` now emits compact protobuf events for `raw-logs` and `raw-spans`.

`incident-processor` consumes these topics and persists them for grouping and correlation. Log/trace volume can grow quickly, so a naive single-table strategy will degrade write throughput and query performance.

### Decision

Use PostgreSQL partitioned append-only raw telemetry tables as the immediate storage strategy in MVP:

- `raw_logs` partitioned by `event_time`
- `raw_spans` partitioned by `event_time`
- monthly range partitions
- minimal, high-value indexes only
- JSONB for log attributes

Consumer behavior:

- parse protobuf payloads from Kafka (`byte[]`)
- perform explicit inserts into raw tables
- keep listener logic thin; move grouping logic to dedicated processing service methods

### Rationale

- Fits current MVP stack and dependencies.
- Keeps ingestion path explicit and debuggable.
- Partitioning + selective indexing handles significantly higher write rates than unpartitioned tables.
- Preserves future path to move raw telemetry to ClickHouse when sustained volume justifies it.

### Consequences

Positive:

- predictable write behavior under growth
- bounded index sizes per partition
- simpler retention operations by dropping old partitions
- clear schema for downstream grouping and observability

Trade-offs:

- operational overhead for partition lifecycle management
- PostgreSQL is still not ideal for very long-term high-cardinality raw telemetry analytics
- eventual migration/offload to ClickHouse may still be required

### Operational Guardrails

- automate partition creation and expiry (for example, monthly creation + retention drop)
- keep raw data retention bounded (for example 7 to 30 days based on volume)
- scale Kafka consumers horizontally by partition count
- add batched inserts or async write buffers when insert latency becomes bottleneck
- monitor ingest lag, write latency, and table/index growth

### Evolution Path

1. Start with partitioned PostgreSQL raw tables.
2. Add batched writes in `incident-processor` if throughput pressure appears.
3. Introduce ClickHouse as cold/high-volume analytics store when justified by metrics.
4. Keep PostgreSQL for incident state and hot operational queries.

