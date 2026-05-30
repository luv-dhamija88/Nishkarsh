# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.nishkarsh.incident-processor' is invalid and this project uses 'com.nishkarsh.incident_processor' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.6/maven-plugin/build-image.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/4.0.6/reference/web/reactive.html)
* [R2DBC API](https://docs.spring.io/spring-boot/4.0.6/reference/data/sql.html#data.sql.r2dbc)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/4.0.6/reference/messaging/kafka.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

### Additional Links
These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Current Status

`incident-processor` now implements the first raw telemetry ingestion slice.

- Main class: `com.nishkarsh.incident_processor.IncidentProcessorApplication`
- Default HTTP port: `8082`
- Kafka listeners consume compact protobuf events from `raw-logs` and `raw-spans`
- Raw telemetry is persisted into PostgreSQL through reactive `DatabaseClient` writes
- Flyway now manages schema setup from `src/main/resources/db/migration`
- Current migration bootstrap file: `V1__raw_telemetry_tables.sql`
- Grouping and incident lifecycle logic are still the next slice

## Running Locally

Run from IntelliJ using the `IncidentProcessorApplication` main class, or from terminal:

```bash
mvn spring-boot:run
```

By default the service expects both reactive PostgreSQL access and Flyway JDBC access. Override them with environment variables when needed:

```bash
export SPRING_R2DBC_URL="r2dbc:postgresql://localhost:5432/nishkarsh"
export SPRING_R2DBC_USERNAME="postgres"
export SPRING_R2DBC_PASSWORD="postgres"
export SPRING_FLYWAY_URL="jdbc:postgresql://localhost:5432/nishkarsh"
export SPRING_FLYWAY_USER="postgres"
export SPRING_FLYWAY_PASSWORD="postgres"
```

If you want to run it alongside `ingestion-service`, `8082` is already the default port. To change it anyway:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"
```

If you import the monorepo root in IntelliJ, also import `apps/incident-processor/pom.xml` as a Maven project so IntelliJ creates a runnable module.

