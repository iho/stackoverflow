package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.web.dto.MeResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;

@Deprecated
public class MeResponseConverter implements Converter<User, MeResponse> {
    @Override
    public MeResponse convert(User source) {
        Set<String> roles = source.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        return new MeResponse(
                source.getUsername(),
                source.getEmail(),
                source.getReputation(),
                roles,
                source.getCreatedAt()
        );
    }
}
