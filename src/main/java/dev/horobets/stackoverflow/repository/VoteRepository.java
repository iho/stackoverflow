package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.model.vote.Vote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPostTypeAndPostIdAndUser_Id(PostType postType, Long postId, Long userId);
}

