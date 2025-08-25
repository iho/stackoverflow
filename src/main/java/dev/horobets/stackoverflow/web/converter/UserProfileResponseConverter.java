package dev.horobets.stackoverflow.web.converter;

import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.AnswerRepository;
import dev.horobets.stackoverflow.repository.BookmarkRepository;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.UserBadgeRepository;
import dev.horobets.stackoverflow.web.dto.UserProfileResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserProfileResponseConverter implements Converter<User, UserProfileResponse> {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserBadgeRepository userBadgeRepository;

    public UserProfileResponseConverter(QuestionRepository questionRepository,
                                        AnswerRepository answerRepository,
                                        BookmarkRepository bookmarkRepository,
                                        UserBadgeRepository userBadgeRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public UserProfileResponse convert(User user) {
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

        return new UserProfileResponse(
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
    }
}

