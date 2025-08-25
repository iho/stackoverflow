package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.service.AnswerService;
import dev.horobets.stackoverflow.web.dto.AnswerRequest;
import dev.horobets.stackoverflow.web.dto.AnswerResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import dev.horobets.stackoverflow.web.mapper.AnswerMapper;

@RestController
@RequestMapping("/api")
public class AnswerController {

    private final AnswerService answerService;
    private final AnswerMapper answerMapper;

    public AnswerController(AnswerService answerService, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
    }

    @GetMapping("/questions/{questionId}/answers")
    public ResponseEntity<Page<AnswerResponse>> list(@PathVariable Long questionId, Pageable pageable) {
        Page<Answer> page = answerService.listByQuestion(questionId, pageable);
        return ResponseEntity.ok(page.map(answerMapper::toResponse));
    }

    @GetMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> get(@PathVariable Long id) {
        Answer a = answerService.getById(id);
        return ResponseEntity.ok(answerMapper.toResponse(a));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerResponse> create(@PathVariable Long questionId,
                                                 @RequestBody @Valid AnswerRequest request,
                                                 @AuthenticationPrincipal UserDetails principal) {
        Answer a = answerService.create(questionId, request, principal.getUsername());
        AnswerResponse body = answerMapper.toResponse(a);
        return ResponseEntity.created(URI.create("/api/answers/" + a.getId())).body(body);
    }

    @PutMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> update(@PathVariable Long id,
                                                 @RequestBody @Valid AnswerRequest request,
                                                 @AuthenticationPrincipal UserDetails principal) {
        Answer a = answerService.update(id, request, principal.getUsername());
        return ResponseEntity.ok(answerMapper.toResponse(a));
    }

    @DeleteMapping("/answers/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal) {
        answerService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
