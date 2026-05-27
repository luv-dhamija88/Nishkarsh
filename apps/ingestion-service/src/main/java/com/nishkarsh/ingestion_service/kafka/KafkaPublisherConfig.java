package com.nishkarsh.ingestion_service.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaPublisherConfig {

	@Bean
	public ProducerFactory<String, byte[]> telemetryKafkaProducerFactory(
			@Value("${nishkarsh.kafka.publisher.bootstrap-servers:localhost:9092}") String bootstrapServers,
			@Value("${nishkarsh.kafka.publisher.client-id:ingestion-service-telemetry-publisher}") String clientId,
			@Value("${nishkarsh.kafka.publisher.compression-type:lz4}") String compressionType,
			@Value("${nishkarsh.kafka.publisher.acks:1}") String acks,
			@Value("${nishkarsh.kafka.publisher.batch-size:65536}") int batchSize,
			@Value("${nishkarsh.kafka.publisher.linger-ms:25}") int lingerMs,
			@Value("${nishkarsh.kafka.publisher.request-timeout-ms:30000}") int requestTimeoutMs,
			@Value("${nishkarsh.kafka.publisher.delivery-timeout-ms:120000}") int deliveryTimeoutMs,
			@Value("${nishkarsh.kafka.publisher.max-in-flight-requests-per-connection:5}")
				int maxInFlightRequestsPerConnection) {
		return new DefaultKafkaProducerFactory<>(producerConfigs(
			bootstrapServers,
			clientId,
			compressionType,
			acks,
			batchSize,
			lingerMs,
			requestTimeoutMs,
			deliveryTimeoutMs,
			maxInFlightRequestsPerConnection
		));
	}

	@Bean
	public KafkaTemplate<String, byte[]> telemetryKafkaTemplate(
			ProducerFactory<String, byte[]> telemetryKafkaProducerFactory) {
		return new KafkaTemplate<>(telemetryKafkaProducerFactory);
	}

	static Map<String, Object> producerConfigs(
			String bootstrapServers,
			String clientId,
			String compressionType,
			String acks,
			int batchSize,
			int lingerMs,
			int requestTimeoutMs,
			int deliveryTimeoutMs,
			int maxInFlightRequestsPerConnection) {
		Map<String, Object> configs = new HashMap<>();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configs.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		configs.put(ProducerConfig.ACKS_CONFIG, acks);
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
		configs.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
		configs.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
		configs.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
		configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
		configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
		configs.put(
			ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
			maxInFlightRequestsPerConnection
		);
		return configs;
	}
}


