package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.post.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    long countByUser_Id(Long userId);

    @Query("select coalesce(sum(q.voteCount),0) from Question q where q.user.id = :userId")
    long sumVoteCountByUserId(@Param("userId") Long userId);

    @Query("select coalesce(sum(q.views),0) from Question q where q.user.id = :userId")
    long sumViewsByUserId(@Param("userId") Long userId);
}

