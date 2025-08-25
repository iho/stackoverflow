package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;
import java.util.Set;

public record UserProfileResponse(
        String username,
        String about,
        int reputation,
        Set<String> roles,
        Instant createdAt,
        Instant lastLogin,
        long questionCount,
        long answerCount,
        long acceptedAnswerCount,
        long votesReceived,
        long questionViews,
        long bookmarkCount,
        long badgeCount
) {}
