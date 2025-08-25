package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.tag.Tag;
import dev.horobets.stackoverflow.web.dto.TagResponse;
import org.springframework.core.convert.converter.Converter;

@Deprecated
public class TagResponseConverter implements Converter<Tag, TagResponse> {
    @Override
    public TagResponse convert(Tag t) {
        return new TagResponse(
                t.getId(),
                t.getName(),
                t.getDescription(),
                t.getQuestionCount(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
