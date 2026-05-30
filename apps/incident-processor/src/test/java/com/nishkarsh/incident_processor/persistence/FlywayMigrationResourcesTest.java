package com.nishkarsh.incident_processor.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class FlywayMigrationResourcesTest {

	@Test
	void rawTelemetryMigrationExistsAndDefinesRequiredTables() throws IOException {
		ClassPathResource migration = new ClassPathResource("db/migration/V1__raw_telemetry_tables.sql");

		assertThat(migration.exists()).isTrue();
		String sql = new String(migration.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		assertThat(sql).contains("CREATE TABLE IF NOT EXISTS raw_logs");
		assertThat(sql).contains("CREATE TABLE IF NOT EXISTS raw_spans");
		assertThat(sql).contains("CREATE TABLE IF NOT EXISTS raw_logs_default");
		assertThat(sql).contains("CREATE TABLE IF NOT EXISTS raw_spans_default");
	}
}

