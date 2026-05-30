package com.nishkarsh.incident_processor.kafka;

import com.nishkarsh.incident_processor.kafka.proto.RawLogEvent;
import com.nishkarsh.incident_processor.kafka.proto.RawSpanEvent;
import com.nishkarsh.incident_processor.persistence.RawTelemetryPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "nishkarsh.raw-telemetry.enabled", havingValue = "true", matchIfMissing = true)
public class RawTelemetryEventHandler {

	private static final Logger log = LoggerFactory.getLogger(RawTelemetryEventHandler.class);

	private final RawTelemetryPersistenceService persistenceService;

	public RawTelemetryEventHandler(RawTelemetryPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void handleRawLogEvent(String key, RawLogEvent event) {
		persistenceService.persistRawLogEvent(key, event)
			.doOnError(ex -> log.error(
				"failed persisting raw-log event key={} eventId={} service={}",
				key,
				event.getEventId(),
				event.getServiceName(),
				ex
			))
			.block();

		log.info(
			"persisted raw-log event key={} eventId={} traceId={} service={} severity={}",
			key,
			event.getEventId(),
			event.getTraceId(),
			event.getServiceName(),
			event.getSeverity()
		);
	}

	public void handleRawSpanEvent(String key, RawSpanEvent event) {
		persistenceService.persistRawSpanEvent(key, event)
			.doOnError(ex -> log.error(
				"failed persisting raw-span event key={} eventId={} service={}",
				key,
				event.getEventId(),
				event.getServiceName(),
				ex
			))
			.block();

		log.info(
			"persisted raw-span event key={} eventId={} traceId={} spanId={} service={} status={}",
			key,
			event.getEventId(),
			event.getTraceId(),
			event.getSpanId(),
			event.getServiceName(),
			event.getStatusCode()
		);
	}
}

