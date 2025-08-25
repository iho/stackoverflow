package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.post.Comment;
import dev.horobets.stackoverflow.model.post.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostTypeAndPostIdOrderByCreatedAtAsc(PostType postType, Long postId);
}

