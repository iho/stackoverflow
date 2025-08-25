package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.post.Comment;
import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.service.CommentService;
import dev.horobets.stackoverflow.web.dto.CommentRequest;
import dev.horobets.stackoverflow.web.dto.CommentResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final ConversionService conversionService;

    public CommentController(CommentService commentService, ConversionService conversionService) {
        this.commentService = commentService;
        this.conversionService = conversionService;
    }

    @GetMapping("/questions/{questionId}/comments")
    public ResponseEntity<List<CommentResponse>> listForQuestion(@PathVariable Long questionId) {
        List<Comment> list = commentService.list(PostType.QUESTION, questionId);
        return ResponseEntity.ok(list.stream().map(c -> conversionService.convert(c, CommentResponse.class)).toList());
    }

    @GetMapping("/answers/{answerId}/comments")
    public ResponseEntity<List<CommentResponse>> listForAnswer(@PathVariable Long answerId) {
        List<Comment> list = commentService.list(PostType.ANSWER, answerId);
        return ResponseEntity.ok(list.stream().map(c -> conversionService.convert(c, CommentResponse.class)).toList());
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> get(@PathVariable Long id) {
        Comment c = commentService.getById(id);
        return ResponseEntity.ok(conversionService.convert(c, CommentResponse.class));
    }

    @PostMapping("/questions/{questionId}/comments")
    public ResponseEntity<CommentResponse> createForQuestion(@PathVariable Long questionId,
                                                             @RequestBody @Valid CommentRequest request,
                                                             @AuthenticationPrincipal UserDetails principal) {
        Comment c = commentService.createForQuestion(questionId, request, principal.getUsername());
        CommentResponse body = conversionService.convert(c, CommentResponse.class);
        return ResponseEntity.created(URI.create("/api/comments/" + c.getId())).body(body);
    }

    @PostMapping("/answers/{answerId}/comments")
    public ResponseEntity<CommentResponse> createForAnswer(@PathVariable Long answerId,
                                                           @RequestBody @Valid CommentRequest request,
                                                           @AuthenticationPrincipal UserDetails principal) {
        Comment c = commentService.createForAnswer(answerId, request, principal.getUsername());
        CommentResponse body = conversionService.convert(c, CommentResponse.class);
        return ResponseEntity.created(URI.create("/api/comments/" + c.getId())).body(body);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id,
                                                  @RequestBody @Valid CommentRequest request,
                                                  @AuthenticationPrincipal UserDetails principal) {
        Comment c = commentService.update(id, request, principal.getUsername());
        return ResponseEntity.ok(conversionService.convert(c, CommentResponse.class));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal) {
        commentService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}

