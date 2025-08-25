package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.service.QuestionService;
import dev.horobets.stackoverflow.web.dto.QuestionRequest;
import dev.horobets.stackoverflow.web.dto.QuestionResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import dev.horobets.stackoverflow.web.mapper.QuestionMapper;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    public QuestionController(QuestionService questionService, QuestionMapper questionMapper) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> list(Pageable pageable) {
        Page<Question> page = questionService.list(pageable);
        Page<QuestionResponse> mapped = page.map(questionMapper::toResponse);
        return ResponseEntity.ok(mapped);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id) {
        Question q = questionService.getById(id);
        return ResponseEntity.ok(questionMapper.toResponse(q));
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(@RequestBody @Valid QuestionRequest request,
                                                   @AuthenticationPrincipal UserDetails principal) {
        Question q = questionService.create(request, principal.getUsername());
        QuestionResponse body = questionMapper.toResponse(q);
        return ResponseEntity.created(URI.create("/api/questions/" + q.getId())).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> update(@PathVariable Long id,
                                                   @RequestBody @Valid QuestionRequest request,
                                                   @AuthenticationPrincipal UserDetails principal) {
        Question q = questionService.update(id, request, principal.getUsername());
        return ResponseEntity.ok(questionMapper.toResponse(q));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal) {
        questionService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
