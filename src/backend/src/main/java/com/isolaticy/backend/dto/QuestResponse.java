package com.isolaticy.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record QuestResponse(
        UUID id,
        String title,
        String description,
        String category,
        Instant createdAt) {
}