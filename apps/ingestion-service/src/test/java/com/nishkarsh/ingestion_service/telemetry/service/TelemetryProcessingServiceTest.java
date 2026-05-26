package com.nishkarsh.ingestion_service.telemetry.service;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
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

	@Test
	void processLogsExport_allowsMissingAttributesByLeavingThemNull() {
		TelemetryProcessingService service = new TelemetryProcessingService();

		LogRecord logRecord = LogRecord.newBuilder()
			.setSeverityText("INFO")
			.setTimeUnixNano(1000L)
			.build();

		ExportLogsServiceRequest request = ExportLogsServiceRequest.newBuilder()
			.addResourceLogs(ResourceLogs.newBuilder()
				.addScopeLogs(ScopeLogs.newBuilder().addLogRecords(logRecord).build())
				.build())
			.build();

		service.processLogsExport(request).block();
	}
}
