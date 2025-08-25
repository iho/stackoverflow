package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(@NotBlank String body) {}

