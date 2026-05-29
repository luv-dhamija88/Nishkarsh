package com.nishkarsh.incident_processor.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nishkarsh.incident_processor.kafka.proto.RawLogEvent;
import com.nishkarsh.incident_processor.kafka.proto.RawSpanEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "nishkarsh.raw-telemetry.enabled", havingValue = "true", matchIfMissing = true)
public class RawTelemetryKafkaListener {

	private final RawTelemetryEventHandler eventHandler;

	public RawTelemetryKafkaListener(RawTelemetryEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@KafkaListener(
		topics = "${nishkarsh.kafka.topics.raw-logs}",
		groupId = "${nishkarsh.kafka.consumer.group-id}"
	)
	public void onRawLogMessage(
			@Payload byte[] payload,
			@Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
		RawLogEvent event = parseRawLogEvent(payload, key);
		eventHandler.handleRawLogEvent(key, event);
	}

	@KafkaListener(
		topics = "${nishkarsh.kafka.topics.raw-spans}",
		groupId = "${nishkarsh.kafka.consumer.group-id}"
	)
	public void onRawSpanMessage(
			@Payload byte[] payload,
			@Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
		RawSpanEvent event = parseRawSpanEvent(payload, key);
		eventHandler.handleRawSpanEvent(key, event);
	}

	static RawLogEvent parseRawLogEvent(byte[] payload, String key) {
		try {
			return RawLogEvent.parseFrom(payload);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException(
				"Invalid protobuf payload on topic raw-logs for key=" + key,
				ex
			);
		}
	}

	static RawSpanEvent parseRawSpanEvent(byte[] payload, String key) {
		try {
			return RawSpanEvent.parseFrom(payload);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException(
				"Invalid protobuf payload on topic raw-spans for key=" + key,
				ex
			);
		}
	}
}


