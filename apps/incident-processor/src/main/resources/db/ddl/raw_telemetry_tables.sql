-- Reference raw telemetry storage schema for incident-processor.
--
-- Design goals:
-- 1) Keep ingestion append-only and simple.
-- 2) Partition by event_time to handle high write volume.
-- 3) Index only high-value query dimensions.
--
-- Source of truth now lives in db/migration/V1__raw_telemetry_tables.sql.
-- NOTE: Create future partitions via scheduled job (or pg_partman) in production.
-- A DEFAULT partition is kept as a safety net so inserts do not fail if a new month
-- arrives before partition maintenance runs.

CREATE TABLE IF NOT EXISTS raw_logs (
    event_id UUID NOT NULL,
    trace_id TEXT,
    span_id TEXT,
    service_name TEXT NOT NULL,
    severity TEXT,
    message TEXT,
    body TEXT,
    exception_type TEXT,
    exception_message TEXT,
    event_time TIMESTAMPTZ NOT NULL,
    attributes JSONB NOT NULL DEFAULT '{}'::jsonb,
    kafka_key TEXT,
    ingested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (event_time, event_id)
) PARTITION BY RANGE (event_time);

CREATE TABLE IF NOT EXISTS raw_spans (
    event_id UUID NOT NULL,
    trace_id TEXT,
    span_id TEXT,
    parent_span_id TEXT,
    service_name TEXT NOT NULL,
    operation_name TEXT,
    duration_ms BIGINT NOT NULL,
    status_code TEXT NOT NULL,
    event_time TIMESTAMPTZ NOT NULL,
    kafka_key TEXT,
    ingested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (event_time, event_id)
) PARTITION BY RANGE (event_time);

-- Example monthly partitions for current bootstrap month.
CREATE TABLE IF NOT EXISTS raw_logs_2026_05
    PARTITION OF raw_logs
    FOR VALUES FROM ('2026-05-01T00:00:00Z') TO ('2026-06-01T00:00:00Z');

CREATE TABLE IF NOT EXISTS raw_spans_2026_05
    PARTITION OF raw_spans
    FOR VALUES FROM ('2026-05-01T00:00:00Z') TO ('2026-06-01T00:00:00Z');

CREATE TABLE IF NOT EXISTS raw_logs_default
    PARTITION OF raw_logs DEFAULT;

CREATE TABLE IF NOT EXISTS raw_spans_default
    PARTITION OF raw_spans DEFAULT;

CREATE INDEX IF NOT EXISTS idx_raw_logs_service_time
    ON raw_logs (service_name, event_time DESC);

CREATE INDEX IF NOT EXISTS idx_raw_logs_trace_id
    ON raw_logs (trace_id);

CREATE INDEX IF NOT EXISTS idx_raw_logs_attributes_gin
    ON raw_logs USING GIN (attributes);

CREATE INDEX IF NOT EXISTS idx_raw_spans_service_time
    ON raw_spans (service_name, event_time DESC);

CREATE INDEX IF NOT EXISTS idx_raw_spans_trace_id
    ON raw_spans (trace_id);

