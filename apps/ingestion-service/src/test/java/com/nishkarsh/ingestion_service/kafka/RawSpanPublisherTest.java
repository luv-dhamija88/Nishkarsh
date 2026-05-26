package com.nishkarsh.ingestion_service.kafka;

import com.nishkarsh.ingestion_service.kafka.proto.RawSpanEvent;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetrySpan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RawSpanPublisherTest {

	@Test
	void toProto_mapsAllRequiredFieldsCorrectly() {
		TelemetrySpan span = TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder()
				.traceId("abc")
				.spanId("123")
				.parentSpanId("456")
				.build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder()
				.name("db.query")
				.duration(2810_000_000L)
				.statusCode(2)
				.build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder()
				.serviceName("payment-service")
				.build())
			.build();

		RawSpanEvent event = RawSpanPublisher.toProto(span);

		assertNotNull(event.getEventId());
		assertFalse(event.getEventId().isEmpty());
		assertEquals("abc", event.getTraceId());
		assertEquals("123", event.getSpanId());
		assertEquals("456", event.getParentSpanId());
		assertEquals("payment-service", event.getServiceName());
		assertEquals("db.query", event.getOperationName());
		assertEquals(2810L, event.getDurationMs());
		assertEquals("ERROR", event.getStatusCode());
	}

	@Test
	void toProto_statusCodeOk_mapsToOkString() {
		TelemetrySpan span = TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder().build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder().statusCode(1).build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder().build())
			.build();

		assertEquals("OK", RawSpanPublisher.toProto(span).getStatusCode());
	}

	@Test
	void toProto_nullStatusCode_defaultsToUnset() {
		TelemetrySpan span = TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder().build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder().build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder().build())
			.build();

		assertEquals("UNSET", RawSpanPublisher.toProto(span).getStatusCode());
	}

	@Test
	void toProto_nullDuration_defaultsToZero() {
		TelemetrySpan span = TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder().build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder().build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder().build())
			.build();

		assertEquals(0L, RawSpanPublisher.toProto(span).getDurationMs());
	}

	@Test
	void toProto_producesNonEmptyByteArray() {
		TelemetrySpan span = TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder().traceId("t1").spanId("s1").build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder().name("op").statusCode(1).build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder().serviceName("svc").build())
			.build();

		byte[] bytes = RawSpanPublisher.toProto(span).toByteArray();

		assertNotNull(bytes);
		assertFalse(bytes.length == 0);
	}
}

