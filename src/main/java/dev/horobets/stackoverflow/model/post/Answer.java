package dev.horobets.stackoverflow.model.post;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Lob
    @NotBlank
    @Column(nullable = false)
    private String body;

    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Answer() {}

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
