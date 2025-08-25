package dev.horobets.stackoverflow.service;

import dev.horobets.stackoverflow.model.post.Comment;
import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.CommentRepository;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.AnswerRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.CommentRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          QuestionRepository questionRepository,
                          AnswerRepository answerRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> list(PostType postType, Long postId) {
        return commentRepository.findAllByPostTypeAndPostIdOrderByCreatedAtAsc(postType, postId);
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }

    @Transactional
    public Comment createForQuestion(Long questionId, CommentRequest request, String username) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        return create(PostType.QUESTION, q.getId(), request, username);
    }

    @Transactional
    public Comment createForAnswer(Long answerId, CommentRequest request, String username) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found"));
        return create(PostType.ANSWER, a.getId(), request, username);
    }

    @Transactional
    public Comment create(PostType type, Long postId, CommentRequest request, String username) {
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Comment c = new Comment();
        c.setBody(request.body());
        c.setPostType(type);
        c.setPostId(postId);
        c.setUser(user);
        return commentRepository.save(c);
    }

    @Transactional
    public Comment update(Long id, CommentRequest request, String username) {
        Comment c = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, c)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        c.setBody(request.body());
        return commentRepository.save(c);
    }

    @Transactional
    public void delete(Long id, String username) {
        Comment c = getById(id);
        User user = userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!canModify(user, c)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        commentRepository.delete(c);
    }

    private boolean canModify(User user, Comment c) {
        if (c.getUser() != null && c.getUser().getId().equals(user.getId())) return true;
        return user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_MODERATOR || r.getName() == RoleName.ROLE_ADMIN);
    }
}

