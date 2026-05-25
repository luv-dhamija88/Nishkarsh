package com.nishkarsh.ingestion_service.telemetry.service;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

class TelemetryProcessingServiceTest {

	@Test
	void processTraceExport_allowsMissingAttributesByLeavingThemNull() {
		TelemetryProcessingService service = new TelemetryProcessingService();

		Span span = Span.newBuilder()
			.setName("test-operation")
			.setStartTimeUnixNano(1000L)
			.setEndTimeUnixNano(2000L)
			.build();

		ExportTraceServiceRequest request = ExportTraceServiceRequest.newBuilder()
			.addResourceSpans(ResourceSpans.newBuilder()
				.addScopeSpans(ScopeSpans.newBuilder().addSpans(span).build())
				.build())
			.build();

		service.processTraceExport(request).block();
	}
}
