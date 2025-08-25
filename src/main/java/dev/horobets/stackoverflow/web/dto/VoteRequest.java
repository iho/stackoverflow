package dev.horobets.stackoverflow.web.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(@NotNull Integer voteValue) {}

