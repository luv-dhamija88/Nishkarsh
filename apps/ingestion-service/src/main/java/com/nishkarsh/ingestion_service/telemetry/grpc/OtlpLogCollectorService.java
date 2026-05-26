package com.nishkarsh.ingestion_service.telemetry.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc;
import com.nishkarsh.ingestion_service.telemetry.service.TelemetryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtlpLogCollectorService extends LogsServiceGrpc.LogsServiceImplBase {

	private final TelemetryProcessingService telemetryProcessingService;

	@Override
	public void export(
			ExportLogsServiceRequest request,
			StreamObserver<ExportLogsServiceResponse> responseObserver) {

		telemetryProcessingService.processLogsExport(request)
			.subscribe(
				ignored -> {
				},
				error -> {
					log.error("Failed to process OTLP logs export", error);
					responseObserver.onError(
						Status.INTERNAL
							.withDescription("Failed to process OTLP logs export")
							.withCause(error)
							.asRuntimeException()
					);
				},
				() -> {
					responseObserver.onNext(ExportLogsServiceResponse.newBuilder().build());
					responseObserver.onCompleted();
				}
			);
	}
}

