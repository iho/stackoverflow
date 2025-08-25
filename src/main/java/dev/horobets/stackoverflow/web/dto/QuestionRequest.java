package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record QuestionRequest(
        @NotBlank(message = "{validation.notblank}")
        @Size(min = 5, max = 200, message = "{validation.size}") String title,
        @NotBlank(message = "{validation.notblank}") String body,
        Set<String> tags
) {}
