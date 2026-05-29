## ADR 006: Flyway Schema Migrations for Incident Processor

Date: 2026-05-27

### Context

`incident-processor` now persists raw Kafka telemetry into PostgreSQL tables.

The schema already exists as SQL under `src/main/resources/db/ddl`, but relying on manual execution makes local setup brittle and creates drift risk between environments.

The service also uses R2DBC for runtime writes, which means schema management needs a separate startup path using JDBC.

### Decision

Use Flyway as the schema migration mechanism for `apps/incident-processor`.

- keep reactive persistence on R2DBC for application writes
- use Flyway with PostgreSQL JDBC configuration for startup migrations
- store versioned SQL under `src/main/resources/db/migration`
- start with `V1__raw_telemetry_tables.sql` for raw logs and spans
- enable `baseline-on-migrate` so existing local databases that were initialized manually do not fail immediately when Flyway is introduced

### Rationale

- smallest useful production-style migration tool for a Spring Boot + PostgreSQL service
- easy for local demos and IntelliJ runs
- avoids custom schema bootstrap code inside the application
- keeps SQL explicit and reviewable
- supports future additive schema evolution for incident grouping tables

### Consequences

Positive:

- repeatable database setup across environments
- version history for schema changes
- fewer manual onboarding steps
- cleaner path to evolve raw telemetry storage

Trade-offs:

- introduces both R2DBC and JDBC configuration into the service
- local developers need matching `SPRING_FLYWAY_*` settings if defaults are not used
- existing databases may need cleanup if they diverged from the expected V1 structure

### Notes

- Flyway migrations are the source of truth for runtime schema creation.
- `db/ddl/raw_telemetry_tables.sql` remains as a readable reference copy for architecture discussion.
- Monthly partitions should still be created automatically over time; the migration includes default partitions as a safety net so writes do not fail before that automation exists.

