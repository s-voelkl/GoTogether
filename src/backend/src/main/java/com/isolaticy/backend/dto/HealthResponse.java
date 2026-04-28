package com.isolaticy.backend.dto;

public record HealthResponse(String status, String service, String version) {
}