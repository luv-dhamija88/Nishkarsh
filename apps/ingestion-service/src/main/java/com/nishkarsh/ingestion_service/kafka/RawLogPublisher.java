package com.nishkarsh.ingestion_service.kafka;

import com.nishkarsh.ingestion_service.kafka.proto.RawLogEvent;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetryLog;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawLogPublisher {

	static final String TOPIC = "raw-logs";

	private final KafkaTemplate<String, byte[]> telemetryKafkaTemplate;

	public Mono<Void> publish(TelemetryLog telemetryLog) {
		return Mono.fromCallable(() -> toProto(telemetryLog).toByteArray())
			.flatMap(payload -> Mono.fromFuture(
				() -> telemetryKafkaTemplate.send(TOPIC, partitionKey(telemetryLog), payload)
			))
			.doOnSuccess(result -> log.debug(
				"published raw-log traceId={} spanId={} service={}",
				telemetryLog.getTraceId(),
				telemetryLog.getSpanId(),
				telemetryLog.getServiceName()
			))
			.doOnError(ex -> log.error(
				"failed to publish raw-log traceId={} service={}",
				telemetryLog.getTraceId(),
				telemetryLog.getServiceName(),
				ex
			))
			.then();
	}

	private static String partitionKey(TelemetryLog telemetryLog) {
		String serviceName = telemetryLog.getServiceName();
		return serviceName != null ? serviceName : "unknown";
	}

	static RawLogEvent toProto(TelemetryLog telemetryLog) {
		RawLogEvent.Builder builder = RawLogEvent.newBuilder()
			.setEventId(UUID.randomUUID().toString())
			.setTraceId(safe(telemetryLog.getTraceId()))
			.setSpanId(safe(telemetryLog.getSpanId()))
			.setServiceName(safe(telemetryLog.getServiceName()))
			.setSeverity(safe(telemetryLog.getSeverityText()))
			.setMessage(resolveMessage(telemetryLog))
			.setBody(safe(telemetryLog.getBody()))
			.setExceptionType(safe(telemetryLog.getExceptionType()))
			.setExceptionMessage(safe(telemetryLog.getExceptionMessage()))
			.setTimestamp(resolveTimestamp(telemetryLog.getTimeUnixNano()));

		putAttribute(builder, "deployment.environment", telemetryLog.getDeploymentEnvironment());
		putAttribute(builder, "host.name", telemetryLog.getHostName());
		putAttribute(builder, "k8s.pod.name", telemetryLog.getK8sPodName());
		putAttribute(builder, "thread.name", telemetryLog.getThreadName());

		return builder.build();
	}

	private static String resolveMessage(TelemetryLog telemetryLog) {
		if (telemetryLog.getLogMessage() != null) {
			return telemetryLog.getLogMessage();
		}
		return safe(telemetryLog.getBody());
	}

	private static String resolveTimestamp(Long timeUnixNano) {
		if (timeUnixNano == null || timeUnixNano == 0L) {
			return Instant.now().toString();
		}
		return Instant.ofEpochSecond(
			timeUnixNano / 1_000_000_000L,
			timeUnixNano % 1_000_000_000L
		).toString();
	}

	private static void putAttribute(RawLogEvent.Builder builder, String key, String value) {
		if (value != null) {
			builder.putAttributes(key, value);
		}
	}

	private static String safe(String value) {
		return value != null ? value : "";
	}
}

