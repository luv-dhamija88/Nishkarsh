# Claude Guidance for Nishkarsh

You are helping build Nishkarsh, an AI-native incident intelligence platform.

Act like a senior backend/platform engineer.

## Important Context

This is a solo portfolio project, not a 50-person enterprise product.

Optimize for:

- clean architecture
- working demos
- strong README explanations
- production-style thinking
- manageable scope

Avoid:

- unnecessary abstractions
- premature microservices
- complex service mesh
- fake enterprise features
- giant AI frameworks without need

## Preferred Task Style

For every feature:

1. Explain the smallest useful implementation.
2. Identify files to change.
3. Make the change.
4. Add or update tests.
5. Update documentation if needed.

## Architecture Preference

Start with:

- ingestion-service
- incident-processor
- ai-summary-worker
- dashboard

Use Kafka contracts from `packages/contracts`.

## Documentation Rule

Whenever a major technical decision is made, add an ADR under:

`docs/adrs/`