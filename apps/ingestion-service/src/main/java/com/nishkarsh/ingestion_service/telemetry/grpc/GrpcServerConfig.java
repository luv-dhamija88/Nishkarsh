package com.nishkarsh.ingestion_service.telemetry.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcServerConfig {

	@Value("${telemetry.collector.grpc.port:4317}")
	private int grpcPort;

	@Bean(destroyMethod = "shutdown")
	public Server otelCollectorGrpcServer(OtlpTraceCollectorService traceCollectorService) throws IOException {
		Server server = NettyServerBuilder
			.forPort(grpcPort)
			.addService(traceCollectorService)
			.build()
			.start();

		log.info("Custom OTEL gRPC collector started on port {}", grpcPort);
		return server;
	}
}
