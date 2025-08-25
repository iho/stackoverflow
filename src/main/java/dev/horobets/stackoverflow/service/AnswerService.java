package dev.horobets.stackoverflow.service;

import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.AnswerRepository;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.AnswerRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public Page<Answer> listByQuestion(Long questionId, Pageable pageable) {
        return answerRepository.findAllByQuestion_IdOrderByCreatedAtAsc(questionId, pageable);
    }

    public Answer getById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found"));
    }

    @Transactional
    public Answer create(Long questionId, AnswerRequest request, String username) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Answer a = new Answer();
        a.setBody(request.body());
        a.setUser(user);
        a.setQuestion(q);
        Answer saved = answerRepository.save(a);
        q.setAnswerCount(q.getAnswerCount() + 1);
        questionRepository.save(q);
        return saved;
    }

    @Transactional
    public Answer update(Long id, AnswerRequest request, String username) {
        Answer a = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, a)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        a.setBody(request.body());
        return answerRepository.save(a);
    }

    @Transactional
    public void delete(Long id, String username) {
        Answer a = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, a)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        Question q = a.getQuestion();
        answerRepository.delete(a);
        if (q != null) {
            q.setAnswerCount(Math.max(0, q.getAnswerCount() - 1));
            questionRepository.save(q);
        }
    }

    private boolean canModify(User user, Answer a) {
        if (a.getUser() != null && a.getUser().getId().equals(user.getId())) return true;
        return user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_MODERATOR || r.getName() == RoleName.ROLE_ADMIN);
    }
}

