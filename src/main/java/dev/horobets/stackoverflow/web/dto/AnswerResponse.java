package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;

public record AnswerResponse(
        Long id,
        String body,
        int voteCount,
        boolean accepted,
        Long questionId,
        Long authorId,
        String authorUsername,
        Instant createdAt,
        Instant updatedAt
) {}

