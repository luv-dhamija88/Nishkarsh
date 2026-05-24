# Nishkarsh

AI-native incident intelligence platform that transforms noisy production logs into actionable summaries, anomaly insights, and probable root causes.

## Current Goal

Build a portfolio-grade AI log summarizer focused on:

- log ingestion
- Kafka-based async processing
- incident grouping
- AI-generated incident summaries
- observability with OpenTelemetry
- production-style reliability patterns

## Architecture

Initial architecture:

```text
Log Client
  -> Ingestion Service
  -> Kafka
  -> Incident Processor
  -> PostgreSQL
  -> AI Summary Worker
  -> Dashboard