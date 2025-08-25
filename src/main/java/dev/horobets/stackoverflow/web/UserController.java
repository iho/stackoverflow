package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.UserProfileResponse;
import dev.horobets.stackoverflow.web.assembler.UserProfileAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserProfileAssembler userProfileAssembler;

    public UserController(UserRepository userRepository, UserProfileAssembler userProfileAssembler) {
        this.userRepository = userRepository;
        this.userProfileAssembler = userProfileAssembler;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String username) {
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UserProfileResponse body = userProfileAssembler.toProfile(user);
        return ResponseEntity.ok(body);
    }
}
