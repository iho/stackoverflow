package dev.horobets.stackoverflow.web.dto;

import dev.horobets.stackoverflow.model.post.PostType;

public record VoteResponse(PostType postType, Long postId, int total, Integer myVote) {}

