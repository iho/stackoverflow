package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(@NotBlank String body) {}

