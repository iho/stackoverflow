package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.*;
import dev.horobets.stackoverflow.web.dto.UserProfileResponse;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserBadgeRepository userBadgeRepository;

    public UserController(UserRepository userRepository,
                          QuestionRepository questionRepository,
                          AnswerRepository answerRepository,
                          BookmarkRepository bookmarkRepository,
                          UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String username) {
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Long userId = user.getId();
        long questionCount = questionRepository.countByUser_Id(userId);
        long answerCount = answerRepository.countByUser_Id(userId);
        long acceptedAnswerCount = answerRepository.countByUser_IdAndAcceptedTrue(userId);
        long questionVotes = questionRepository.sumVoteCountByUserId(userId);
        long answerVotes = answerRepository.sumVoteCountByUserId(userId);
        long votesReceived = questionVotes + answerVotes;
        long questionViews = questionRepository.sumViewsByUserId(userId);
        long bookmarkCount = bookmarkRepository.countByUser_Id(userId);
        long badgeCount = userBadgeRepository.countByUser_Id(userId);

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        UserProfileResponse body = new UserProfileResponse(
                user.getUsername(),
                user.getAbout(),
                user.getReputation(),
                roles,
                user.getCreatedAt(),
                user.getLastLogin(),
                questionCount,
                answerCount,
                acceptedAnswerCount,
                votesReceived,
                questionViews,
                bookmarkCount,
                badgeCount
        );

        return ResponseEntity.ok(body);
    }
}

