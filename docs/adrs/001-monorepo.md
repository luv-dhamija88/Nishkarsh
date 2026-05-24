## ADR 001: Monorepo for MVP Delivery

Date: 2026-05-24

### Context

Nishkarsh is being built as a portfolio-grade incident intelligence platform, but the immediate goal is to deliver a working MVP quickly.

At this stage, development is handled by a single engineer responsible for backend services, AI worker flows, contracts, infra, documentation, and dashboard iteration.

Splitting the system into many repositories this early would add coordination overhead (version alignment, cross-repo changes, duplicated tooling, and release friction) that does not provide meaningful value for the current scope.

### Decision

Use a monorepo structure for the MVP.

Keep logical boundaries using folders and package contracts (apps, packages/contracts, docs, infra), while keeping all code in one repository.

### Rationale

The monorepo is chosen primarily to reduce clutter and cognitive load for a single developer managing the full stack.

This enables:

- Faster end-to-end iteration across ingestion, processing, AI summary generation, and dashboard flows.
- Atomic changes when one feature touches contracts, services, and docs together.
- Simpler local setup, testing, and debugging in one workspace.
- Lower operational overhead during MVP phase (fewer pipelines and repo-level permissions to manage).

### Consequences

Positive:

- Higher delivery speed for MVP milestones.
- Better visibility across architecture and dependencies.
- Easier consistency in coding standards and documentation.

Trade-offs:

- Repository can grow noisy without discipline.
- CI may become slower as the codebase expands.

Mitigations:

- Preserve strict package boundaries and ownership-by-folder.
- Use path-based CI and targeted test execution.
- Revisit repo split only when team size, ownership boundaries, or deployment cadence require it.
