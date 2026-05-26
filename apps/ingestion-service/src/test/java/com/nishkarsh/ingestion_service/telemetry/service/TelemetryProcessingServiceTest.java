package com.nishkarsh.ingestion_service.telemetry.service;

import com.nishkarsh.ingestion_service.kafka.RawLogPublisher;
import com.nishkarsh.ingestion_service.kafka.RawSpanPublisher;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetryLog;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetrySpan;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TelemetryProcessingServiceTest {

	@Mock
	private RawLogPublisher rawLogPublisher;

	@Mock
	private RawSpanPublisher rawSpanPublisher;

	private TelemetryProcessingService service;

	@BeforeEach
	void setUp() {
		when(rawLogPublisher.publish(any(TelemetryLog.class))).thenReturn(Mono.empty());
		when(rawSpanPublisher.publish(any(TelemetrySpan.class))).thenReturn(Mono.empty());
		service = new TelemetryProcessingService(rawLogPublisher, rawSpanPublisher);
	}

	@Test
	void processTraceExport_allowsMissingAttributesByLeavingThemNull() {
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
