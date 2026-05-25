package com.nishkarsh.ingestion_service.telemetry.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import com.nishkarsh.ingestion_service.telemetry.service.TelemetryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtlpTraceCollectorService extends TraceServiceGrpc.TraceServiceImplBase {

	private final TelemetryProcessingService telemetryProcessingService;

	@Override
	public void export(
			ExportTraceServiceRequest request,
			StreamObserver<ExportTraceServiceResponse> responseObserver) {

		telemetryProcessingService.processTraceExport(request)
			.subscribe(
				ignored -> {
				},
				error -> {
					log.error("Failed to process OTLP trace export", error);
					responseObserver.onError(
						Status.INTERNAL
							.withDescription("Failed to process OTLP trace export")
							.withCause(error)
							.asRuntimeException()
					);
				},
				() -> {
					responseObserver.onNext(ExportTraceServiceResponse.newBuilder().build());
					responseObserver.onCompleted();
				}
			);
	}
}
