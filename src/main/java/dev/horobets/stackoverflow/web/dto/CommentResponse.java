package dev.horobets.stackoverflow.web.dto;

import dev.horobets.stackoverflow.model.post.PostType;
import java.time.Instant;

public record CommentResponse(
        Long id,
        String body,
        int voteCount,
        PostType postType,
        Long postId,
        Long authorId,
        String authorUsername,
        Instant createdAt,
        Instant updatedAt
) {}

