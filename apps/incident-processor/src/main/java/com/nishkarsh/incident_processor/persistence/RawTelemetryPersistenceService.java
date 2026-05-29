package com.nishkarsh.incident_processor.persistence;

import com.nishkarsh.incident_processor.kafka.proto.RawLogEvent;
import com.nishkarsh.incident_processor.kafka.proto.RawSpanEvent;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(name = "nishkarsh.raw-telemetry.enabled", havingValue = "true", matchIfMissing = true)
public class RawTelemetryPersistenceService {

	private static final Logger log = LoggerFactory.getLogger(RawTelemetryPersistenceService.class);

	private final DatabaseClient databaseClient;

	public RawTelemetryPersistenceService(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	public Mono<Void> persistRawLogEvent(String key, RawLogEvent event) {
		Instant eventTime = parseEventTime(event.getTimestamp());
		String attributesJson = toJson(event.getAttributesMap());
		String sql = """
			INSERT INTO raw_logs (
				event_id,
				trace_id,
				span_id,
				service_name,
				severity,
				message,
				body,
				exception_type,
				exception_message,
				event_time,
				attributes,
				kafka_key
			)
			VALUES (
				:eventId::uuid,
				:traceId,
				:spanId,
				:serviceName,
				:severity,
				:message,
				:body,
				:exceptionType,
				:exceptionMessage,
				:eventTime,
				CAST(:attributes AS jsonb),
				:kafkaKey
			)
			""";
		return databaseClient.sql(sql)
			.bind("eventId", event.getEventId())
			.bind("traceId", event.getTraceId())
			.bind("spanId", event.getSpanId())
			.bind("serviceName", event.getServiceName())
			.bind("severity", event.getSeverity())
			.bind("message", event.getMessage())
			.bind("body", event.getBody())
			.bind("exceptionType", event.getExceptionType())
			.bind("exceptionMessage", event.getExceptionMessage())
			.bind("eventTime", eventTime)
			.bind("attributes", attributesJson)
			.bind("kafkaKey", nullSafe(key))
			.fetch()
			.rowsUpdated()
			.doOnNext(rows -> log.debug(
				"persisted raw-log eventId={} service={} rowsUpdated={}",
				event.getEventId(),
				event.getServiceName(),
				rows
			))
			.then();
	}

	public Mono<Void> persistRawSpanEvent(String key, RawSpanEvent event) {
		Instant eventTime = Instant.now();
		String sql = """
			INSERT INTO raw_spans (
				event_id,
				trace_id,
				span_id,
				parent_span_id,
				service_name,
				operation_name,
				duration_ms,
				status_code,
				event_time,
				kafka_key
			)
			VALUES (
				:eventId::uuid,
				:traceId,
				:spanId,
				:parentSpanId,
				:serviceName,
				:operationName,
				:durationMs,
				:statusCode,
				:eventTime,
				:kafkaKey
			)
			""";
		return databaseClient.sql(sql)
			.bind("eventId", event.getEventId())
			.bind("traceId", event.getTraceId())
			.bind("spanId", event.getSpanId())
			.bind("parentSpanId", event.getParentSpanId())
			.bind("serviceName", event.getServiceName())
			.bind("operationName", event.getOperationName())
			.bind("durationMs", event.getDurationMs())
			.bind("statusCode", event.getStatusCode())
			.bind("eventTime", eventTime)
			.bind("kafkaKey", nullSafe(key))
			.fetch()
			.rowsUpdated()
			.doOnNext(rows -> log.debug(
				"persisted raw-span eventId={} service={} rowsUpdated={}",
				event.getEventId(),
				event.getServiceName(),
				rows
			))
			.then();
	}

	static Instant parseEventTime(String timestamp) {
		if (timestamp == null || timestamp.isBlank()) {
			return Instant.now();
		}
		try {
			return Instant.parse(timestamp);
		}
		catch (DateTimeParseException ex) {
			return Instant.now();
		}
	}

	String toJson(Map<String, String> attributes) {
		StringBuilder json = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			if (!first) {
				json.append(',');
			}
			json.append('"').append(escapeJson(entry.getKey())).append('"')
				.append(':')
				.append('"').append(escapeJson(entry.getValue())).append('"');
			first = false;
		}
		json.append('}');
		return json.toString();
	}

	private static String escapeJson(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	private static String nullSafe(String value) {
		return value != null ? value : "";
	}
}




