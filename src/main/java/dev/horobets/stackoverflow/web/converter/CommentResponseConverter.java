package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.post.Comment;
import dev.horobets.stackoverflow.web.dto.CommentResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommentResponseConverter implements Converter<Comment, CommentResponse> {
    @Override
    public CommentResponse convert(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getBody(),
                c.getVoteCount(),
                c.getPostType(),
                c.getPostId(),
                c.getUser() != null ? c.getUser().getId() : null,
                c.getUser() != null ? c.getUser().getUsername() : null,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}

