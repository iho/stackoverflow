package dev.horobets.stackoverflow.service;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.QuestionRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public Page<Question> list(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
    }

    @Transactional
    public Question create(QuestionRequest req, String username) {
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Question q = new Question();
        q.setTitle(req.title());
        q.setBody(req.body());
        q.setUser(user);
        return questionRepository.save(q);
    }

    @Transactional
    public Question update(Long id, QuestionRequest req, String username) {
        Question q = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, q)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        q.setTitle(req.title());
        q.setBody(req.body());
        return questionRepository.save(q);
    }

    @Transactional
    public void delete(Long id, String username) {
        Question q = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, q)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        questionRepository.delete(q); // soft-delete via @SQLDelete
    }

    private boolean canModify(User user, Question q) {
        if (q.getUser() != null && q.getUser().getId().equals(user.getId())) {
            return true; // owner
        }
        return user.getRoles().stream().anyMatch(r ->
                r.getName() == RoleName.ROLE_MODERATOR || r.getName() == RoleName.ROLE_ADMIN);
    }
}

