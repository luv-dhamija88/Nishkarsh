## ADR 002: Java/Python Service Boundary for MVP

Date: 2026-05-24

### Context

Nishkarsh is an AI-native incident intelligence platform built as a portfolio-grade MVP by a solo engineer.

The MVP must demonstrate strong distributed systems and backend engineering through ingestion, Kafka-based processing, incident grouping, and reliable query APIs, while also including practical AI summarization.

Using one language for everything is possible, but it creates tradeoffs:

- Pure Java would slow AI iteration and prompt experimentation.
- Pure Python would reduce leverage from the Java/Spring ecosystem for event-driven backend services.

To avoid overengineering, language boundaries should map directly to problem boundaries.

### Decision

Use Java (Spring Boot) for all core backend and distributed systems services.

Use Python only for AI-related workflows in `ai-summary-worker`.

Boundary definition:

- Java owns ingestion-service, incident-processor, and query-service.
- Python owns prompt construction, OpenAI API integration, summary generation, and AI-specific response shaping.
- Service integration occurs through Kafka topics and PostgreSQL records, not cross-language shared runtime code.

### Rationale

Why Java for backend services:

- Strong Spring Boot support for robust API, Kafka, and PostgreSQL integration.
- Good fit for reliability-focused event processing and explicit failure handling.
- Cleaner long-term maintainability for core platform logic and contracts.

Why Python for AI worker:

- Faster iteration loop for prompt and output-format changes.
- Mature AI SDK ecosystem and simpler experimentation for model interaction.
- Keeps AI concerns isolated from core incident processing path.

Why this split is correct for MVP:

- Preserves architecture clarity without introducing unnecessary abstraction.
- Optimizes for delivery speed with the smallest practical polyglot scope.
- Prevents AI-specific churn from destabilizing backend service foundations.

### Consequences

Positive:

- Clear ownership boundaries by responsibility, not by team size.
- Faster AI iteration while preserving backend operational rigor.
- Easier to reason about failures and observability per domain.

Trade-offs:

- Polyglot repository increases tooling and CI complexity.
- Two dependency stacks must be maintained.

Mitigations:

- Keep Python scope narrow to AI worker only.
- Use explicit API/event contracts in `packages/contracts`.
- Keep integration testing focused on event contracts and DB interfaces.

### Rejected Alternatives

All Java:

- Rejected for slower AI iteration and less ergonomic AI workflow development.

All Python:

- Rejected for reduced leverage in Spring/Kafka-centric backend architecture goals.

Mixed language usage within every service:

- Rejected as unnecessary complexity for MVP and difficult to operate as a solo engineer.

### Guardrails

- Do not add non-AI Python services in v0.1.
- Do not move incident grouping or core event-processing logic out of Java.
- Keep AI worker asynchronous and downstream of `incident-created`.
- Keep failure isolation: AI issues must not block ingestion or incident creation.

### Review Trigger

Revisit this decision only if one of the following happens:

- AI workload evolves into multiple independent pipelines requiring additional ownership boundaries.
- Measured throughput or operational constraints indicate the boundary is limiting scale.
- Team composition changes and justified ownership splits emerge.
