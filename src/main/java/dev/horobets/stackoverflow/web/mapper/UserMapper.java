package dev.horobets.stackoverflow.web.mapper;

import dev.horobets.stackoverflow.model.user.Role;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.web.dto.MeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToNames")
    MeResponse toMeResponse(User user);

    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(r -> r.getName().name()).collect(Collectors.toSet());
    }
}

