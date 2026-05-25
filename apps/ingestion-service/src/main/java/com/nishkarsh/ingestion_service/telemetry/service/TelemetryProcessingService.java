package com.nishkarsh.ingestion_service.telemetry.service;

import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.emptyToNull;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.getBoolean;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.getLong;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.getString;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.nullSafe;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.toHex;
import static com.nishkarsh.ingestion_service.telemetry.service.TelemetryAttributeHelper.toMap;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.Span;
import java.util.Map;
import com.nishkarsh.ingestion_service.telemetry.model.TelemetrySpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TelemetryProcessingService {

	public Mono<Void> processTraceExport(ExportTraceServiceRequest request) {
		return Flux.fromIterable(request.getResourceSpansList())
			.flatMap(this::processResourceSpan)
			.then();
	}

	private Flux<TelemetrySpan> processResourceSpan(ResourceSpans resourceSpans) {
		Map<String, AnyValue> resourceMap = toMap(resourceSpans.getResource().getAttributesList());
		return Flux.fromIterable(resourceSpans.getScopeSpansList())
			.flatMap(scopeSpan -> Flux.fromIterable(scopeSpan.getSpansList())
				.map(span -> toTelemetrySpan(resourceMap, span))
				.doOnNext(tspan -> log.info(
					"received span traceId={} spanId={} service={} name={}",
					nullSafe(tspan.getTraceContext() != null ? tspan.getTraceContext().getTraceId() : null),
					nullSafe(tspan.getTraceContext() != null ? tspan.getTraceContext().getSpanId() : null),
					nullSafe(tspan.getResourceAttributes() != null ? tspan.getResourceAttributes().getServiceName() : null),
					nullSafe(tspan.getSpanMetadata() != null ? tspan.getSpanMetadata().getName() : null)
				)));
	}

	private TelemetrySpan toTelemetrySpan(Map<String, AnyValue> resourceMap, Span span) {
		Map<String, AnyValue> spanMap = toMap(span.getAttributesList());
		Long start = span.getStartTimeUnixNano();
		Long end = span.getEndTimeUnixNano();

		return TelemetrySpan.builder()
			.traceContext(TelemetrySpan.TraceContext.builder()
				.traceId(toHex(span.getTraceId()))
				.spanId(toHex(span.getSpanId()))
				.parentSpanId(toHex(span.getParentSpanId()))
				.traceState(emptyToNull(span.getTraceState()))
				.traceFlags((long) span.getFlags())
				.build())
			.spanMetadata(TelemetrySpan.SpanMetadata.builder()
				.name(emptyToNull(span.getName()))
				.kind(span.getKind().name())
				.startTimeUnixNano(start)
				.endTimeUnixNano(end)
				.duration((start != null && end != null) ? Math.max(0L, end - start) : null)
				.statusCode(span.getStatus().getCodeValue())
				.statusMessage(emptyToNull(span.getStatus().getMessage()))
				.errorType(getString(spanMap, "error.type"))
				.build())
			.resourceAttributes(TelemetrySpan.ResourceAttributes.builder()
				.serviceName(getString(resourceMap, "service.name"))
				.serviceNamespace(getString(resourceMap, "service.namespace"))
				.serviceInstanceId(getString(resourceMap, "service.instance.id"))
				.serviceVersion(getString(resourceMap, "service.version"))
				.deploymentEnvironment(getString(resourceMap, "deployment.environment"))
				.hostId(getString(resourceMap, "host.id"))
				.hostName(getString(resourceMap, "host.name"))
				.hostArch(getString(resourceMap, "host.arch"))
				.containerId(getString(resourceMap, "container.id"))
				.containerName(getString(resourceMap, "container.name"))
				.containerImageName(getString(resourceMap, "container.image.name"))
				.containerImageTag(getString(resourceMap, "container.image.tag"))
				.k8sClusterName(getString(resourceMap, "k8s.cluster.name"))
				.k8sNamespaceName(getString(resourceMap, "k8s.namespace.name"))
				.k8sNodeName(getString(resourceMap, "k8s.node.name"))
				.k8sPodName(getString(resourceMap, "k8s.pod.name"))
				.k8sPodUid(getString(resourceMap, "k8s.pod.uid"))
				.k8sContainerName(getString(resourceMap, "k8s.container.name"))
				.cloudProvider(getString(resourceMap, "cloud.provider"))
				.cloudPlatform(getString(resourceMap, "cloud.platform"))
				.cloudRegion(getString(resourceMap, "cloud.region"))
				.cloudAvailabilityZone(getString(resourceMap, "cloud.availability_zone"))
				.processPid(getLong(resourceMap, "process.pid"))
				.processExecutableName(getString(resourceMap, "process.executable.name"))
				.processRuntimeName(getString(resourceMap, "process.runtime.name"))
				.processRuntimeVersion(getString(resourceMap, "process.runtime.version"))
				.processRuntimeDescription(getString(resourceMap, "process.runtime.description"))
				.telemetrySdkLanguage(getString(resourceMap, "telemetry.sdk.language"))
				.telemetrySdkName(getString(resourceMap, "telemetry.sdk.name"))
				.telemetrySdkVersion(getString(resourceMap, "telemetry.sdk.version"))
				.build())
			.httpAttributes(TelemetrySpan.HttpAttributes.builder()
				.httpRequestMethod(getString(spanMap, "http.request.method"))
				.httpResponseStatusCode(getLong(spanMap, "http.response.status_code"))
				.httpRoute(getString(spanMap, "http.route"))
				.urlPath(getString(spanMap, "url.path"))
				.urlScheme(getString(spanMap, "url.scheme"))
				.serverPort(getLong(spanMap, "server.port"))
				.clientPort(getLong(spanMap, "client.port"))
				.userAgentOriginal(getString(spanMap, "user_agent.original"))
				.httpRequestBodySize(getLong(spanMap, "http.request.body.size"))
				.httpResponseBodySize(getLong(spanMap, "http.response.body.size"))
				.build())
			.rpcAttributes(TelemetrySpan.RpcAttributes.builder()
				.rpcSystem(getString(spanMap, "rpc.system"))
				.rpcService(getString(spanMap, "rpc.service"))
				.rpcMethod(getString(spanMap, "rpc.method"))
				.rpcGrpcStatusCode(getLong(spanMap, "rpc.grpc.status_code"))
				.build())
			.websocketAttributes(TelemetrySpan.WebsocketAttributes.builder()
				.networkTransport(getString(spanMap, "network.transport"))
				.networkProtocolName(getString(spanMap, "network.protocol.name"))
				.networkProtocolVersion(getString(spanMap, "network.protocol.version"))
				.build())
			.databaseAttributes(TelemetrySpan.DatabaseAttributes.builder()
				.dbSystem(getString(spanMap, "db.system"))
				.dbName(getString(spanMap, "db.name"))
				.dbOperationName(getString(spanMap, "db.operation.name"))
				.dbQueryText(getString(spanMap, "db.query.text"))
				.dbCollectionName(getString(spanMap, "db.collection.name"))
				.dbNamespace(getString(spanMap, "db.namespace"))
				.dbResponseStatusCode(getString(spanMap, "db.response.status_code"))
				.build())
			.messagingAttributes(TelemetrySpan.MessagingAttributes.builder()
				.messagingSystem(getString(spanMap, "messaging.system"))
				.messagingOperation(getString(spanMap, "messaging.operation"))
				.messagingDestinationName(getString(spanMap, "messaging.destination.name"))
				.messagingDestinationPartitionId(getLong(spanMap, "messaging.destination.partition.id"))
				.messagingKafkaConsumerGroup(getString(spanMap, "messaging.kafka.consumer.group"))
				.messagingKafkaMessageOffset(getLong(spanMap, "messaging.kafka.message.offset"))
				.messagingKafkaMessageKey(getString(spanMap, "messaging.kafka.message.key"))
				.messagingMessageId(getString(spanMap, "messaging.message.id"))
				.messagingMessageBodySize(getLong(spanMap, "messaging.message.body.size"))
				.messagingBatchMessageCount(getLong(spanMap, "messaging.batch.message_count"))
				.messagingClientId(getString(spanMap, "messaging.client.id"))
				.build())
			.asyncWorkerAttributes(TelemetrySpan.AsyncWorkerAttributes.builder()
				.threadId(getLong(spanMap, "thread.id"))
				.threadName(getString(spanMap, "thread.name"))
				.processPid(getLong(resourceMap, "process.pid"))
				.codeFunctionName(getString(spanMap, "code.function.name"))
				.codeNamespace(getString(spanMap, "code.namespace"))
				.codeFilePath(getString(spanMap, "code.file.path"))
				.codeLineNumber(getLong(spanMap, "code.line.number"))
				.build())
			.jobAttributes(TelemetrySpan.JobAttributes.builder()
				.jobName(getString(spanMap, "job.name"))
				.jobRunId(getString(spanMap, "job.run.id"))
				.faasTrigger(getString(spanMap, "faas.trigger"))
				.build())
			.exceptionAttributes(TelemetrySpan.ExceptionAttributes.builder()
				.exceptionType(getString(spanMap, "exception.type"))
				.exceptionMessage(getString(spanMap, "exception.message"))
				.exceptionStacktrace(getString(spanMap, "exception.stacktrace"))
				.exceptionEscaped(getBoolean(spanMap, "exception.escaped"))
				.build())
			.networkAttributes(TelemetrySpan.NetworkAttributes.builder()
				.serverAddress(getString(spanMap, "server.address"))
				.clientAddress(getString(spanMap, "client.address"))
				.networkTransport(getString(spanMap, "network.transport"))
				.networkType(getString(spanMap, "network.type"))
				.networkProtocolName(getString(spanMap, "network.protocol.name"))
				.networkProtocolVersion(getString(spanMap, "network.protocol.version"))
				.networkLocalAddress(getString(spanMap, "network.local.address"))
				.networkLocalPort(getLong(spanMap, "network.local.port"))
				.networkPeerAddress(getString(spanMap, "network.peer.address"))
				.networkPeerPort(getLong(spanMap, "network.peer.port"))
				.build())
			.securityAttributes(TelemetrySpan.SecurityAttributes.builder()
				.clientAddress(getString(spanMap, "client.address"))
				.serverAddress(getString(spanMap, "server.address"))
				.userAgentOriginal(getString(spanMap, "user_agent.original"))
				.build())
			.receivedTimestamp(System.currentTimeMillis())
			.build();
	}
}