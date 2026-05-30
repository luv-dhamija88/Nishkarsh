package com.nishkarsh.incident_processor.kafka;

import com.nishkarsh.incident_processor.kafka.proto.RawLogEvent;
import com.nishkarsh.incident_processor.kafka.proto.RawSpanEvent;
import com.nishkarsh.incident_processor.persistence.RawTelemetryPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RawTelemetryEventHandlerTest {

	@Mock
	private RawTelemetryPersistenceService persistenceService;

	@Test
	void handleRawLogEvent_persistsEvent() {
		RawTelemetryEventHandler handler = new RawTelemetryEventHandler(persistenceService);
		RawLogEvent event = RawLogEvent.newBuilder()
			.setEventId("7f11d84a-14da-4f45-a2ab-b7db14a894f4")
			.setServiceName("payment-service")
			.setSeverity("ERROR")
			.build();

		when(persistenceService.persistRawLogEvent("payment-service", event)).thenReturn(Mono.empty());

		handler.handleRawLogEvent("payment-service", event);

		verify(persistenceService).persistRawLogEvent("payment-service", event);
	}

	@Test
	void handleRawSpanEvent_persistsEvent() {
		RawTelemetryEventHandler handler = new RawTelemetryEventHandler(persistenceService);
		RawSpanEvent event = RawSpanEvent.newBuilder()
			.setEventId("77f0a95f-b9b0-4f64-98ef-74d4f563dce6")
			.setServiceName("payment-service")
			.setStatusCode("ERROR")
			.build();

		when(persistenceService.persistRawSpanEvent("payment-service", event)).thenReturn(Mono.empty());

		handler.handleRawSpanEvent("payment-service", event);

		verify(persistenceService).persistRawSpanEvent("payment-service", event);
	}
}

