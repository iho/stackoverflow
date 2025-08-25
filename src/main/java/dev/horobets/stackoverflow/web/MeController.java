package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.MeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import dev.horobets.stackoverflow.web.mapper.UserMapper;

@RestController
@RequestMapping("/api")
public class MeController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public MeController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User user = userRepository.findWithRolesByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        MeResponse body = userMapper.toMeResponse(user);
        return ResponseEntity.ok(body);
    }
}
