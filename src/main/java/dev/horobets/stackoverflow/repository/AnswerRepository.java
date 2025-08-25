package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.post.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    long countByUser_Id(Long userId);

    long countByUser_IdAndAcceptedTrue(Long userId);

    @Query("select coalesce(sum(a.voteCount),0) from Answer a where a.user.id = :userId")
    long sumVoteCountByUserId(@Param("userId") Long userId);
}
