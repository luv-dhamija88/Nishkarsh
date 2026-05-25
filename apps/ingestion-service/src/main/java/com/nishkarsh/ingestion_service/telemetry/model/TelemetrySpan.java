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
public class TelemetrySpan {
	private TraceContext traceContext;
	private SpanMetadata spanMetadata;
	private ResourceAttributes resourceAttributes;
	private HttpAttributes httpAttributes;
	private RpcAttributes rpcAttributes;
	private WebsocketAttributes websocketAttributes;
	private DatabaseAttributes databaseAttributes;
	private MessagingAttributes messagingAttributes;
	private AsyncWorkerAttributes asyncWorkerAttributes;
	private JobAttributes jobAttributes;
	private ExceptionAttributes exceptionAttributes;
	private NetworkAttributes networkAttributes;
	private SecurityAttributes securityAttributes;
	private Long receivedTimestamp;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TraceContext {
		private String traceId;
		private String spanId;
		private String parentSpanId;
		private String traceState;
		private Long traceFlags;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SpanMetadata {
		private String name;
		private String kind;
		private Long startTimeUnixNano;
		private Long endTimeUnixNano;
		private Long duration;
		private Integer statusCode;
		private String statusMessage;
		private String errorType;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ResourceAttributes {
		private String serviceName;
		private String serviceNamespace;
		private String serviceInstanceId;
		private String serviceVersion;
		private String deploymentEnvironment;
		private String hostId;
		private String hostName;
		private String hostArch;
		private String containerId;
		private String containerName;
		private String containerImageName;
		private String containerImageTag;
		private String k8sClusterName;
		private String k8sNamespaceName;
		private String k8sNodeName;
		private String k8sPodName;
		private String k8sPodUid;
		private String k8sContainerName;
		private String cloudProvider;
		private String cloudPlatform;
		private String cloudRegion;
		private String cloudAvailabilityZone;
		private Long processPid;
		private String processExecutableName;
		private String processRuntimeName;
		private String processRuntimeVersion;
		private String processRuntimeDescription;
		private String telemetrySdkLanguage;
		private String telemetrySdkName;
		private String telemetrySdkVersion;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class HttpAttributes {
		private String httpRequestMethod;
		private Long httpResponseStatusCode;
		private String httpRoute;
		private String urlPath;
		private String urlScheme;
		private Long serverPort;
		private Long clientPort;
		private String userAgentOriginal;
		private Long httpRequestBodySize;
		private Long httpResponseBodySize;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RpcAttributes {
		private String rpcSystem;
		private String rpcService;
		private String rpcMethod;
		private Long rpcGrpcStatusCode;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class WebsocketAttributes {
		private String networkTransport;
		private String networkProtocolName;
		private String networkProtocolVersion;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DatabaseAttributes {
		private String dbSystem;
		private String dbName;
		private String dbOperationName;
		private String dbQueryText;
		private String dbCollectionName;
		private String dbNamespace;
		private String dbResponseStatusCode;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class MessagingAttributes {
		private String messagingSystem;
		private String messagingOperation;
		private String messagingDestinationName;
		private Long messagingDestinationPartitionId;
		private String messagingKafkaConsumerGroup;
		private Long messagingKafkaMessageOffset;
		private String messagingKafkaMessageKey;
		private String messagingMessageId;
		private Long messagingMessageBodySize;
		private Long messagingBatchMessageCount;
		private String messagingClientId;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AsyncWorkerAttributes {
		private Long threadId;
		private String threadName;
		private Long processPid;
		private String codeFunctionName;
		private String codeNamespace;
		private String codeFilePath;
		private Long codeLineNumber;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class JobAttributes {
		private String jobName;
		private String jobRunId;
		private String faasTrigger;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ExceptionAttributes {
		private String exceptionType;
		private String exceptionMessage;
		private String exceptionStacktrace;
		private Boolean exceptionEscaped;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class NetworkAttributes {
		private String serverAddress;
		private String clientAddress;
		private String networkTransport;
		private String networkType;
		private String networkProtocolName;
		private String networkProtocolVersion;
		private String networkLocalAddress;
		private Long networkLocalPort;
		private String networkPeerAddress;
		private Long networkPeerPort;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SecurityAttributes {
		private String clientAddress;
		private String serverAddress;
		private String userAgentOriginal;
	}
}
