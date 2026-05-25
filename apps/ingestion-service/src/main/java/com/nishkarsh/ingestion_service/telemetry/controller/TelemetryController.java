package com.nishkarsh.ingestion_service.telemetry.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

	@GetMapping("/health")
	public Mono<ResponseEntity<Map<String, Object>>> health() {
		return Mono.just(ResponseEntity.ok(Map.of(
			"status", "UP",
			"collector", "grpc",
			"endpoint", "0.0.0.0:4317"
		)));
	}
}
