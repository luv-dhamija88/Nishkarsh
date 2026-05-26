package com.nishkarsh.ingestion_service.telemetry.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelemetryLog {
	private String traceId;
	private String spanId;
	private Long timeUnixNano;
	private Long observedTimeUnixNano;
	private Long severityNumber;
	private String severityText;
	private String body;
	private String eventName;

	private String serviceName;
	private String serviceNamespace;
	private String serviceInstanceId;
	private String serviceVersion;
	private String deploymentEnvironment;
	private String hostName;
	private String hostId;
	private String containerId;
	private String k8sPodName;

	private String logMessage;
	private String logLevel;
	private String logLogger;
	private String exceptionType;
	private String exceptionMessage;
	private String exceptionStacktrace;
	private String threadName;
	private Long threadId;
	private String serverAddress;
	private String clientAddress;

	private Long receivedTimestamp;
}

