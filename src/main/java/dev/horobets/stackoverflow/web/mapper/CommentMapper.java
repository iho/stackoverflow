package dev.horobets.stackoverflow.web.mapper;

import dev.horobets.stackoverflow.model.post.Comment;
import dev.horobets.stackoverflow.web.dto.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorUsername", source = "user.username")
    CommentResponse toResponse(Comment c);
}

