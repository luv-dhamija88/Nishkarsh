package com.nishkarsh.ingestion_service.kafka;

import com.nishkarsh.ingestion_service.kafka.proto.RawSpanEvent;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetrySpan;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawSpanPublisher {

	static final String TOPIC = "raw-spans";

	private final KafkaTemplate<String, byte[]> telemetryKafkaTemplate;

	public Mono<Void> publish(TelemetrySpan telemetrySpan) {
		return Mono.fromCallable(() -> toProto(telemetrySpan).toByteArray())
			.flatMap(payload -> Mono.fromFuture(
				() -> telemetryKafkaTemplate.send(TOPIC, partitionKey(telemetrySpan), payload)
			))
			.doOnSuccess(result -> log.debug(
				"published raw-span traceId={} spanId={} service={}",
				safeTraceId(telemetrySpan),
				safeSpanId(telemetrySpan),
				safeServiceName(telemetrySpan)
			))
			.doOnError(ex -> log.error(
				"failed to publish raw-span traceId={} service={}",
				safeTraceId(telemetrySpan),
				safeServiceName(telemetrySpan),
				ex
			))
			.then();
	}

	private static String partitionKey(TelemetrySpan telemetrySpan) {
		String serviceName = safeServiceName(telemetrySpan);
		return serviceName != null ? serviceName : "unknown";
	}

	static RawSpanEvent toProto(TelemetrySpan telemetrySpan) {
		TelemetrySpan.TraceContext ctx = telemetrySpan.getTraceContext();
		TelemetrySpan.SpanMetadata meta = telemetrySpan.getSpanMetadata();
		TelemetrySpan.ResourceAttributes resource = telemetrySpan.getResourceAttributes();

		return RawSpanEvent.newBuilder()
			.setEventId(UUID.randomUUID().toString())
			.setTraceId(safe(ctx != null ? ctx.getTraceId() : null))
			.setSpanId(safe(ctx != null ? ctx.getSpanId() : null))
			.setParentSpanId(safe(ctx != null ? ctx.getParentSpanId() : null))
			.setServiceName(safe(resource != null ? resource.getServiceName() : null))
			.setOperationName(safe(meta != null ? meta.getName() : null))
			.setDurationMs(resolveDurationMs(meta))
			.setStatusCode(resolveStatusCode(meta))
			.build();
	}

	private static long resolveDurationMs(TelemetrySpan.SpanMetadata meta) {
		if (meta == null || meta.getDuration() == null) {
			return 0L;
		}
		return meta.getDuration() / 1_000_000L;
	}

	private static String resolveStatusCode(TelemetrySpan.SpanMetadata meta) {
		if (meta == null || meta.getStatusCode() == null) {
			return "UNSET";
		}
		int code = meta.getStatusCode();
		if (code == 1) {
			return "OK";
		}
		if (code == 2) {
			return "ERROR";
		}
		return "UNSET";
	}

	private static String safeTraceId(TelemetrySpan telemetrySpan) {
		TelemetrySpan.TraceContext ctx = telemetrySpan.getTraceContext();
		return ctx != null ? ctx.getTraceId() : null;
	}

	private static String safeSpanId(TelemetrySpan telemetrySpan) {
		TelemetrySpan.TraceContext ctx = telemetrySpan.getTraceContext();
		return ctx != null ? ctx.getSpanId() : null;
	}

	private static String safeServiceName(TelemetrySpan telemetrySpan) {
		TelemetrySpan.ResourceAttributes resource = telemetrySpan.getResourceAttributes();
		return resource != null ? resource.getServiceName() : null;
	}

	private static String safe(String value) {
		return value != null ? value : "";
	}
}

