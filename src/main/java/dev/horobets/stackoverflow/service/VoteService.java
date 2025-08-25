package dev.horobets.stackoverflow.service;

import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.model.vote.Vote;
import dev.horobets.stackoverflow.repository.AnswerRepository;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import dev.horobets.stackoverflow.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public VoteService(VoteRepository voteRepository,
                       QuestionRepository questionRepository,
                       AnswerRepository answerRepository,
                       UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public int upsertVote(PostType postType, Long postId, String username, int voteValue) {
        if (voteValue != 1 && voteValue != -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "voteValue must be 1 or -1");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Vote existing = voteRepository.findByPostTypeAndPostIdAndUser_Id(postType, postId, user.getId()).orElse(null);
        int delta;
        if (existing == null) {
            Vote v = new Vote();
            v.setPostType(postType);
            v.setPostId(postId);
            v.setUser(user);
            v.setVoteValue(voteValue);
            voteRepository.save(v);
            delta = voteValue;
        } else {
            if (existing.getVoteValue() == voteValue) {
                delta = 0; // idempotent
            } else {
                delta = voteValue - existing.getVoteValue();
                existing.setVoteValue(voteValue);
                voteRepository.save(existing);
            }
        }
        return applyDelta(postType, postId, delta);
    }

    @Transactional
    public int deleteVote(PostType postType, Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Vote existing = voteRepository.findByPostTypeAndPostIdAndUser_Id(postType, postId, user.getId())
                .orElse(null);
        if (existing == null) {
            return currentTotal(postType, postId);
        }
        int delta = -existing.getVoteValue();
        voteRepository.delete(existing);
        return applyDelta(postType, postId, delta);
    }

    @Transactional
    public int currentTotal(PostType postType, Long postId) {
        if (postType == PostType.QUESTION) {
            Question q = questionRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
            return q.getVoteCount();
        } else {
            Answer a = answerRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found"));
            return a.getVoteCount();
        }
    }

    private int applyDelta(PostType postType, Long postId, int delta) {
        if (delta == 0) {
            return currentTotal(postType, postId);
        }
        if (postType == PostType.QUESTION) {
            Question q = questionRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
            q.setVoteCount(q.getVoteCount() + delta);
            questionRepository.save(q);
            return q.getVoteCount();
        } else {
            Answer a = answerRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found"));
            a.setVoteCount(a.getVoteCount() + delta);
            answerRepository.save(a);
            return a.getVoteCount();
        }
    }

    @Transactional
    public Integer userVote(PostType postType, Long postId, String username) {
        if (username == null) return null;
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        return voteRepository.findByPostTypeAndPostIdAndUser_Id(postType, postId, user.getId())
                .map(Vote::getVoteValue)
                .orElse(null);
    }
}
