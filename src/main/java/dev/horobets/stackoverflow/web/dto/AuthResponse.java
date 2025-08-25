package dev.horobets.stackoverflow.web.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String email
) { }
