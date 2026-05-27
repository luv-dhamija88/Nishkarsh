package com.nishkarsh.ingestion_service.kafka;

import com.nishkarsh.ingestion_service.kafka.proto.RawLogEvent;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetryLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RawLogPublisherTest {

	@Test
	void toProto_mapsAllRequiredFieldsCorrectly() {
		TelemetryLog log = TelemetryLog.builder()
			.traceId("abc")
			.spanId("xyz")
			.serviceName("payment-service")
			.severityText("ERROR")
			.logMessage("DB timeout")
			.body("Connection pool exhausted while acquiring DB connection")
			.exceptionType("SQLTimeoutException")
			.exceptionMessage("Timeout while waiting for connection")
			.timeUnixNano(1748217600000000000L)
			.deploymentEnvironment("prod")
			.hostName("node-1")
			.k8sPodName("payment-7d89f")
			.threadName("http-nio-8080-exec-4")
			.build();

		RawLogEvent event = RawLogPublisher.toProto(log);

		assertNotNull(event.getEventId());
		assertFalse(event.getEventId().isEmpty());
		assertEquals("abc", event.getTraceId());
		assertEquals("xyz", event.getSpanId());
		assertEquals("payment-service", event.getServiceName());
		assertEquals("ERROR", event.getSeverity());
		assertEquals("DB timeout", event.getMessage());
		assertEquals("Connection pool exhausted while acquiring DB connection", event.getBody());
		assertEquals("SQLTimeoutException", event.getExceptionType());
		assertEquals("Timeout while waiting for connection", event.getExceptionMessage());
		assertFalse(event.getTimestamp().isEmpty());
		assertEquals("prod", event.getAttributesOrThrow("deployment.environment"));
		assertEquals("node-1", event.getAttributesOrThrow("host.name"));
		assertEquals("payment-7d89f", event.getAttributesOrThrow("k8s.pod.name"));
		assertEquals("http-nio-8080-exec-4", event.getAttributesOrThrow("thread.name"));
	}

	@Test
	void toProto_usesBodyAsMessageWhenLogMessageIsAbsent() {
		TelemetryLog log = TelemetryLog.builder()
			.body("fallback body")
			.build();

		RawLogEvent event = RawLogPublisher.toProto(log);

		assertEquals("fallback body", event.getMessage());
	}

	@Test
	void toProto_omitsNullAttributesFromMap() {
		TelemetryLog log = TelemetryLog.builder()
			.deploymentEnvironment("staging")
			.build();

		RawLogEvent event = RawLogPublisher.toProto(log);

		assertEquals(1, event.getAttributesCount());
		assertEquals("staging", event.getAttributesOrThrow("deployment.environment"));
	}

	@Test
	void toProto_producesNonEmptyByteArray() {
		TelemetryLog log = TelemetryLog.builder()
			.traceId("t1")
			.serviceName("svc")
			.severityText("INFO")
			.body("test message")
			.build();

		byte[] bytes = RawLogPublisher.toProto(log).toByteArray();

		assertNotNull(bytes);
		assertFalse(bytes.length == 0);
	}
}

