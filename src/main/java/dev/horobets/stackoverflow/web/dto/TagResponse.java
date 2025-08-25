package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;

public record TagResponse(
        Long id,
        String name,
        String description,
        int questionCount,
        Instant createdAt,
        Instant updatedAt
) {}

