package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.service.VoteService;
import dev.horobets.stackoverflow.web.dto.VoteRequest;
import dev.horobets.stackoverflow.web.dto.VoteResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/{postType}/{postId}")
    public ResponseEntity<VoteResponse> get(@PathVariable String postType,
                                            @PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetails principal) {
        PostType type = parseType(postType);
        int total = voteService.currentTotal(type, postId);
        Integer my = principal != null ? voteService.userVote(type, postId, principal.getUsername()) : null;
        return ResponseEntity.ok(new VoteResponse(type, postId, total, my));
    }

    @PostMapping("/{postType}/{postId}")
    public ResponseEntity<VoteResponse> upsert(@PathVariable String postType,
                                               @PathVariable Long postId,
                                               @RequestBody @Valid VoteRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        PostType type = parseType(postType);
        int total = voteService.upsertVote(type, postId, principal.getUsername(), request.voteValue());
        Integer my = voteService.userVote(type, postId, principal.getUsername());
        return ResponseEntity.ok(new VoteResponse(type, postId, total, my));
    }

    @DeleteMapping("/{postType}/{postId}")
    public ResponseEntity<VoteResponse> delete(@PathVariable String postType,
                                               @PathVariable Long postId,
                                               @AuthenticationPrincipal UserDetails principal) {
        PostType type = parseType(postType);
        int total = voteService.deleteVote(type, postId, principal.getUsername());
        return ResponseEntity.ok(new VoteResponse(type, postId, total, null));
    }

    private PostType parseType(String s) {
        try {
            return PostType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid postType");
        }
    }
}

