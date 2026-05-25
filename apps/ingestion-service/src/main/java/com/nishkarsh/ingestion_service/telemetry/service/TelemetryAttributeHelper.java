package com.nishkarsh.ingestion_service.telemetry.service;

import com.google.protobuf.ByteString;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TelemetryAttributeHelper {

	private TelemetryAttributeHelper() {
	}

	public static Map<String, AnyValue> toMap(List<KeyValue> attributes) {
		Map<String, AnyValue> map = new HashMap<>();
		for (KeyValue keyValue : attributes) {
			map.put(keyValue.getKey(), keyValue.getValue());
		}
		return map;
	}

	public static String getString(Map<String, AnyValue> map, String key) {
		AnyValue value = map.get(key);
		if (value == null) {
			return null;
		}
		if (value.hasStringValue()) {
			return emptyToNull(value.getStringValue());
		}
		if (value.hasIntValue()) {
			return String.valueOf(value.getIntValue());
		}
		if (value.hasBoolValue()) {
			return String.valueOf(value.getBoolValue());
		}
		if (value.hasDoubleValue()) {
			return String.valueOf(value.getDoubleValue());
		}
		return null;
	}

	public static Long getLong(Map<String, AnyValue> map, String key) {
		AnyValue value = map.get(key);
		if (value == null) {
			return null;
		}
		if (value.hasIntValue()) {
			return value.getIntValue();
		}
		if (value.hasStringValue()) {
			try {
				return Long.parseLong(value.getStringValue());
			} catch (NumberFormatException ignore) {
				return null;
			}
		}
		return null;
	}

	public static Boolean getBoolean(Map<String, AnyValue> map, String key) {
		AnyValue value = map.get(key);
		if (value == null) {
			return null;
		}
		if (value.hasBoolValue()) {
			return value.getBoolValue();
		}
		if (value.hasStringValue()) {
			return Boolean.parseBoolean(value.getStringValue());
		}
		return null;
	}

	public static String toHex(ByteString bytes) {
		if (bytes == null || bytes.isEmpty()) {
			return null;
		}
		StringBuilder builder = new StringBuilder(bytes.size() * 2);
		for (byte b : bytes.toByteArray()) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	public static String emptyToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value;
	}

	public static String nullSafe(String value) {
		return value == null ? "null" : value;
	}
}
