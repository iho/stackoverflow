package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{validation.notblank}") String username,
        @NotBlank(message = "{validation.notblank}") String password
) {}
