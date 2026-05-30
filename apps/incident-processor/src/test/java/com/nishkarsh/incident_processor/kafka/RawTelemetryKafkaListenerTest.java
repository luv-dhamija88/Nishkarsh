package com.nishkarsh.incident_processor.kafka;

import com.nishkarsh.incident_processor.kafka.proto.RawLogEvent;
import com.nishkarsh.incident_processor.kafka.proto.RawSpanEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RawTelemetryKafkaListenerTest {

	@Mock
	private RawTelemetryEventHandler eventHandler;

	@Test
	void onRawLogMessage_parsesAndDelegates() {
		RawTelemetryKafkaListener listener = new RawTelemetryKafkaListener(eventHandler);
		RawLogEvent event = RawLogEvent.newBuilder()
			.setEventId("event-1")
			.setTraceId("trace-1")
			.setSpanId("span-1")
			.setServiceName("payment-service")
			.setSeverity("ERROR")
			.build();

		listener.onRawLogMessage(event.toByteArray(), "payment-service");

		ArgumentCaptor<RawLogEvent> captor = ArgumentCaptor.forClass(RawLogEvent.class);
		verify(eventHandler).handleRawLogEvent(org.mockito.ArgumentMatchers.eq("payment-service"), captor.capture());
		assertTrue(captor.getValue().getEventId().equals("event-1"));
	}

	@Test
	void onRawSpanMessage_parsesAndDelegates() {
		RawTelemetryKafkaListener listener = new RawTelemetryKafkaListener(eventHandler);
		RawSpanEvent event = RawSpanEvent.newBuilder()
			.setEventId("event-2")
			.setTraceId("trace-2")
			.setSpanId("span-2")
			.setServiceName("payment-service")
			.setStatusCode("ERROR")
			.build();

		listener.onRawSpanMessage(event.toByteArray(), "payment-service");

		ArgumentCaptor<RawSpanEvent> captor = ArgumentCaptor.forClass(RawSpanEvent.class);
		verify(eventHandler).handleRawSpanEvent(org.mockito.ArgumentMatchers.eq("payment-service"), captor.capture());
		assertTrue(captor.getValue().getEventId().equals("event-2"));
	}

	@Test
	void onRawLogMessage_invalidPayload_throwsAndDoesNotDelegate() {
		RawTelemetryKafkaListener listener = new RawTelemetryKafkaListener(eventHandler);

		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> listener.onRawLogMessage(new byte[] {1, 2, 3}, "bad-key")
		);

		assertTrue(ex.getMessage().contains("raw-logs"));
		verifyNoInteractions(eventHandler);
	}

	@Test
	void onRawSpanMessage_invalidPayload_throwsAndDoesNotDelegate() {
		RawTelemetryKafkaListener listener = new RawTelemetryKafkaListener(eventHandler);

		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> listener.onRawSpanMessage(new byte[] {4, 5, 6}, "bad-key")
		);

		assertTrue(ex.getMessage().contains("raw-spans"));
		verifyNoInteractions(eventHandler);
	}
}

