package dev.horobets.stackoverflow.web.mapper;

import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.web.dto.AnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorUsername", source = "user.username")
    AnswerResponse toResponse(Answer a);
}

