package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.tag.Tag;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.TagRepository;
import dev.horobets.stackoverflow.web.dto.QuestionResponse;
import dev.horobets.stackoverflow.web.dto.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import dev.horobets.stackoverflow.web.mapper.TagMapper;
import dev.horobets.stackoverflow.web.mapper.QuestionMapper;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final TagMapper tagMapper;
    private final QuestionMapper questionMapper;

    public TagController(TagRepository tagRepository, QuestionRepository questionRepository, TagMapper tagMapper, QuestionMapper questionMapper) {
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.tagMapper = tagMapper;
        this.questionMapper = questionMapper;
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> list(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);
        return ResponseEntity.ok(page.map(tagMapper::toResponse));
    }

    @GetMapping("/{name}")
    public ResponseEntity<TagResponse> getByName(@PathVariable String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        return ResponseEntity.ok(tagMapper.toResponse(tag));
    }

    @GetMapping("/{name}/questions")
    public ResponseEntity<Page<QuestionResponse>> questionsByTag(@PathVariable String name, Pageable pageable) {
        Page<Question> page = questionRepository.findAllByTags_NameIgnoreCase(name, pageable);
        return ResponseEntity.ok(page.map(questionMapper::toResponse));
    }
}
