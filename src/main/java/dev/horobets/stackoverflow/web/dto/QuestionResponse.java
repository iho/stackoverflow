package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;
import java.util.Set;

public record QuestionResponse(
        Long id,
        String title,
        String body,
        int voteCount,
        int answerCount,
        long views,
        boolean closed,
        Instant createdAt,
        Instant updatedAt,
        Long authorId,
        String authorUsername,
        Set<String> tags
) {}

