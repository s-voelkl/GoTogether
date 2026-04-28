package com.isolaticy.backend.service;

import com.isolaticy.backend.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    @Value("${app.version:0.0.1-SNAPSHOT}")
    private String version;

    public HealthResponse health() {
        return new HealthResponse("UP", "isolaticy-backend", version);
    }
}