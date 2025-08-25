package dev.horobets.stackoverflow.web.mapper;

import dev.horobets.stackoverflow.model.tag.Tag;
import dev.horobets.stackoverflow.web.dto.TagResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag t);
}

