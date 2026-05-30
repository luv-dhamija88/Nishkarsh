package com.nishkarsh.incident_processor.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RawTelemetryPersistenceServiceTest {

	@Test
	void parseEventTime_validIsoTimestamp_isParsed() {
		Instant parsed = RawTelemetryPersistenceService.parseEventTime("2026-05-27T10:00:00Z");
		assertEquals(Instant.parse("2026-05-27T10:00:00Z"), parsed);
	}

	@Test
	void parseEventTime_invalidTimestamp_fallsBackToNow() {
		Instant parsed = RawTelemetryPersistenceService.parseEventTime("not-a-time");
		assertFalse(parsed.isAfter(Instant.now().plusSeconds(5)));
		assertFalse(parsed.isBefore(Instant.now().minusSeconds(5)));
	}

	@Test
	void toJson_serializesAttributesMap() {
		RawTelemetryPersistenceService service = new RawTelemetryPersistenceService(mock(DatabaseClient.class));

		String json = service.toJson(Map.of("deployment.environment", "prod", "host.name", "node-1"));
		assertTrue(json.contains("deployment.environment"));
		assertTrue(json.contains("host.name"));
	}
}


