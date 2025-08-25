package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(@NotBlank(message = "{validation.notblank}") String body) {}
