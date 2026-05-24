# Nishkarsh Agent Instructions

## Project Identity

Nishkarsh is a portfolio-grade AI incident intelligence platform.

The goal is to demonstrate backend architecture, distributed systems, observability, AI integration, and production engineering maturity.

## Rules

- Do not overengineer.
- Prefer working vertical slices over abstract frameworks.
- Do not introduce microservices unless there is a clear scaling or ownership reason.
- Keep Java responsible for backend/distributed systems.
- Keep Python responsible for AI/ML workflows.
- All changes must be explainable in architecture terms.

## Current Architecture

- Java Spring Boot for ingestion and incident processing.
- Python for AI summary generation and embeddings.
- Kafka for async event flow.
- PostgreSQL for incidents and metadata.
- ClickHouse may be added later for high-volume log analytics.
- Redis may be used for caching, rate limits, and idempotency.

## Coding Standards

- Use clear package boundaries.
- Add tests for business logic.
- Do not commit secrets.
- Prefer explicit errors over silent failures.
- Add logging and traces for important flows.

## Review Guidelines

Before completing any task, verify:

- Does it keep the system simpler?
- Does it preserve service boundaries?
- Does it avoid leaking tenant or sensitive data?
- Does it include basic tests?
- Does it update docs if architecture changed?