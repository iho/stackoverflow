package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record QuestionRequest(
        @NotBlank @Size(min = 5, max = 200) String title,
        @NotBlank String body,
        Set<String> tags
) {}
