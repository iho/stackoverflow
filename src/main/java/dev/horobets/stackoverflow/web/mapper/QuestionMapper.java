package dev.horobets.stackoverflow.web.mapper;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.tag.Tag;
import dev.horobets.stackoverflow.web.dto.QuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToNames")
    QuestionResponse toResponse(Question q);

    @Named("tagsToNames")
    default Set<String> tagsToNames(Set<Tag> tags) {
        if (tags == null) return null;
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }
}

