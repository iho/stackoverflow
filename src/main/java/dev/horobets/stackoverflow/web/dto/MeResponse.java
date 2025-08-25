package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;
import java.util.Set;

public record MeResponse(
        String username,
        String email,
        int reputation,
        Set<String> roles,
        Instant createdAt
) {}
