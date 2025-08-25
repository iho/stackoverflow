package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.web.dto.QuestionResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;

@Deprecated
public class QuestionResponseConverter implements Converter<Question, QuestionResponse> {
    @Override
    public QuestionResponse convert(Question q) {
        Set<String> tagNames = q.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet());
        return new QuestionResponse(
                q.getId(),
                q.getTitle(),
                q.getBody(),
                q.getVoteCount(),
                q.getAnswerCount(),
                q.getViews(),
                q.isClosed(),
                q.getCreatedAt(),
                q.getUpdatedAt(),
                q.getUser() != null ? q.getUser().getId() : null,
                q.getUser() != null ? q.getUser().getUsername() : null,
                tagNames
        );
    }
}
