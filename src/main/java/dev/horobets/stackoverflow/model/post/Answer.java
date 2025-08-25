package dev.horobets.stackoverflow.model.post;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "answers")
@SQLDelete(sql = "update answers set deleted = true where id = ? and version = ?")
@Where(clause = "deleted = false")
public class Answer extends BaseEntity {

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

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
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
