package dev.horobets.stackoverflow.model.vote;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.post.PostType;
import dev.horobets.stackoverflow.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_post_user", columnNames = {"post_type", "post_id", "user_id"})
})
public class Vote extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private PostType postType;

    @NotNull
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "vote_value", nullable = false)
    private int voteValue; // 1 or -1

    public Vote() {}

    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getVoteValue() { return voteValue; }
    public void setVoteValue(int voteValue) { this.voteValue = voteValue; }
}
