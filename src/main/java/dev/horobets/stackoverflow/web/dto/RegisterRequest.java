package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "{validation.notblank}") @Size(min = 3, max = 50, message = "{validation.size}") String username,
        @NotBlank(message = "{validation.notblank}") @Email(message = "{validation.email}") String email,
        @NotBlank(message = "{validation.notblank}") @Size(min = 6, max = 255, message = "{validation.size}") String password
) {}
