package com.isolaticy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestCreateRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 500) String description,
        @NotBlank @Size(max = 80) String category) {
}