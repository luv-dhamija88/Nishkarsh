# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.nishkarsh.ingestion-service' is invalid and this project uses 'com.nishkarsh.ingestion_service' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/maven-plugin/build-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/reference/using/devtools.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/reference/web/reactive.html)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/reference/data/sql.html#data.sql.r2dbc)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)

### Additional Links
These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

## Missing R2DBC Driver

Make sure to include a [R2DBC Driver](https://r2dbc.io/drivers/) to connect to your database.
### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Linting

This module uses Maven Checkstyle to enforce Java linting rules from `config/checkstyle/checkstyle.xml`.

Run lint checks locally:

```bash
./mvnw checkstyle:check
```

Linting also runs automatically during:

```bash
./mvnw verify
```

## OTEL gRPC Collector

`ingestion-service` now includes a custom OTLP collector over gRPC.

- gRPC endpoint: `0.0.0.0:4317`
- Trace service method: `opentelemetry.proto.collector.trace.v1.TraceService/Export`
- Logs service method: `opentelemetry.proto.collector.logs.v1.LogsService/Export`
- Health endpoint: `GET /telemetry/health`

Collector behavior:

- Receives OpenTelemetry trace and log export requests.
- Maps standard semantic-convention attributes to internal telemetry models.
- Keeps missing attributes as `null` (best-effort ingestion).
- Processes payloads with reactive `Mono`/`Flux` flow.

Quick checks:

```bash
mvn clean compile
mvn -Dtest=TelemetryProcessingServiceTest test
```

## Kafka Publisher Configuration

`ingestion-service` now exposes Kafka producer infrastructure for future telemetry/event publishers.

- Compression: `lz4`
- Key serializer: `StringSerializer`
- Value serializer: `ByteArraySerializer`
- Intended payload shape: protobuf messages serialized with `toByteArray()` before publishing

Relevant properties live under `nishkarsh.kafka.publisher.*` in `src/main/resources/application.yml`.

