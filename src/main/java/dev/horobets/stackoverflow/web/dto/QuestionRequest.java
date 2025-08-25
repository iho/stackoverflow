package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionRequest(
        @NotBlank @Size(min = 5, max = 200) String title,
        @NotBlank String body
) {}

