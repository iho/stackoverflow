package dev.horobets.stackoverflow.model.post;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String body;

    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private PostType postType;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment() {}

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
