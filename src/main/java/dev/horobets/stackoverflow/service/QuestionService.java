package dev.horobets.stackoverflow.service;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.tag.Tag;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.TagRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.web.dto.QuestionRequest;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private static final int MAX_TAGS = 5;
    private static final Pattern TAG_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9-]{0,49}$");

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
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
        if (!CollectionUtils.isEmpty(req.tags())) {
            validateTags(req.tags());
            Set<Tag> tags = resolveTags(req.tags());
            q.setTags(tags);
            incrementCounts(tags, 1);
        }
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
        if (req.tags() != null) {
            validateTags(req.tags());
            Set<Tag> oldTags = new HashSet<>(q.getTags());
            Set<Tag> newTags = resolveTags(req.tags());
            // compute deltas
            Set<Tag> toAdd = new HashSet<>(newTags);
            toAdd.removeAll(oldTags);
            Set<Tag> toRemove = new HashSet<>(oldTags);
            toRemove.removeAll(newTags);
            // apply
            q.setTags(newTags);
            incrementCounts(toAdd, 1);
            incrementCounts(toRemove, -1);
        }
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
        // decrement counts for associated tags on soft delete
        incrementCounts(q.getTags(), -1);
        questionRepository.delete(q); // soft-delete via @SQLDelete
    }

    private boolean canModify(User user, Question q) {
        if (q.getUser() != null && Objects.equals(q.getUser().getId(), user.getId())) {
            return true; // owner
        }
        return user.getRoles().stream().anyMatch(r ->
                r.getName() == RoleName.ROLE_MODERATOR || r.getName() == RoleName.ROLE_ADMIN);
    }

    private Set<Tag> resolveTags(Set<String> names) {
        Set<String> cleaned = names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        if (cleaned.isEmpty()) return Set.of();
        List<Tag> existing = tagRepository.findAllByLowerNames(
                cleaned.stream().map(String::toLowerCase).collect(Collectors.toSet())
        );
        Set<String> existingNamesLower = existing.stream()
                .map(t -> t.getName().toLowerCase()).collect(Collectors.toSet());
        Set<String> toCreate = cleaned.stream()
                .map(String::toLowerCase)
                .filter(n -> !existingNamesLower.contains(n))
                .collect(Collectors.toSet());
        Set<Tag> result = new HashSet<>(existing);
        for (String n : toCreate) {
            Tag t = new Tag();
            t.setName(n);
            t.setQuestionCount(0);
            result.add(tagRepository.save(t));
        }
        return result;
    }

    private void incrementCounts(Set<Tag> tags, int delta) {
        if (tags == null || tags.isEmpty() || delta == 0) return;
        for (Tag t : tags) {
            int nc = t.getQuestionCount() + delta;
            t.setQuestionCount(Math.max(0, nc));
            tagRepository.save(t);
        }
    }

    private void validateTags(Set<String> names) {
        if (names.size() > MAX_TAGS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many tags. Max is " + MAX_TAGS);
        }
        for (String raw : names) {
            if (raw == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag cannot be null");
            }
            String n = raw.trim().toLowerCase();
            if (n.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag cannot be empty");
            }
            if (!TAG_PATTERN.matcher(n).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tag: '" + raw + "'. Allowed: a-z, 0-9, hyphen, 1-50 chars, must start with alphanumeric");
            }
        }
    }
}
