package com.nishkarsh.ingestion_service.kafka;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaPublisherConfigTest {

	@Test
	void producerConfigs_useLz4AndByteArraySerialization() {
		Map<String, Object> configs = KafkaPublisherConfig.producerConfigs(
			"localhost:9092",
			"test-publisher",
			"lz4",
			"all",
			65536,
			25,
			30000,
			120000,
			5
		);

		assertEquals("localhost:9092", configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
		assertEquals("test-publisher", configs.get(ProducerConfig.CLIENT_ID_CONFIG));
		assertEquals("lz4", configs.get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
		assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
		assertEquals(ByteArraySerializer.class, configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
	}

	@Test
	void telemetryKafkaTemplate_canBeCreatedForByteArrayPayloads() {
		KafkaPublisherConfig config = new KafkaPublisherConfig();
		ProducerFactory<String, byte[]> producerFactory = config.telemetryKafkaProducerFactory(
			"localhost:9092",
			"test-publisher",
			"lz4",
			"all",
			65536,
			25,
			30000,
			120000,
			5
		);

		KafkaTemplate<String, byte[]> template = config.telemetryKafkaTemplate(producerFactory);
		assertNotNull(template);
	}
}





