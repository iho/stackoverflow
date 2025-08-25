package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.web.dto.AnswerResponse;
import org.springframework.core.convert.converter.Converter;

@Deprecated
public class AnswerResponseConverter implements Converter<Answer, AnswerResponse> {
    @Override
    public AnswerResponse convert(Answer a) {
        return new AnswerResponse(
                a.getId(),
                a.getBody(),
                a.getVoteCount(),
                a.isAccepted(),
                a.getQuestion() != null ? a.getQuestion().getId() : null,
                a.getUser() != null ? a.getUser().getId() : null,
                a.getUser() != null ? a.getUser().getUsername() : null,
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
