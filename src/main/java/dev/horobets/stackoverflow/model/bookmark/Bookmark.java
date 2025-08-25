package dev.horobets.stackoverflow.model.bookmark;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "bookmarks", indexes = {
        @Index(name = "ix_bookmark_user_question", columnList = "user_id,question_id", unique = true),
        @Index(name = "ix_bookmark_user_answer", columnList = "user_id,answer_id", unique = true)
})
public class Bookmark extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public Bookmark() {}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Answer getAnswer() { return answer; }
    public void setAnswer(Answer answer) { this.answer = answer; }
}

