package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;
import java.util.Set;

public class UserProfileResponse {
    private String username;
    private String about;
    private int reputation;
    private Set<String> roles;
    private Instant createdAt;
    private Instant lastLogin;

    private long questionCount;
    private long answerCount;
    private long acceptedAnswerCount;
    private long votesReceived;
    private long questionViews;
    private long bookmarkCount;
    private long badgeCount;

    public UserProfileResponse() {}

    public UserProfileResponse(String username, String about, int reputation, Set<String> roles,
                               Instant createdAt, Instant lastLogin,
                               long questionCount, long answerCount, long acceptedAnswerCount,
                               long votesReceived, long questionViews, long bookmarkCount, long badgeCount) {
        this.username = username;
        this.about = about;
        this.reputation = reputation;
        this.roles = roles;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.questionCount = questionCount;
        this.answerCount = answerCount;
        this.acceptedAnswerCount = acceptedAnswerCount;
        this.votesReceived = votesReceived;
        this.questionViews = questionViews;
        this.bookmarkCount = bookmarkCount;
        this.badgeCount = badgeCount;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getLastLogin() { return lastLogin; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }
    public long getQuestionCount() { return questionCount; }
    public void setQuestionCount(long questionCount) { this.questionCount = questionCount; }
    public long getAnswerCount() { return answerCount; }
    public void setAnswerCount(long answerCount) { this.answerCount = answerCount; }
    public long getAcceptedAnswerCount() { return acceptedAnswerCount; }
    public void setAcceptedAnswerCount(long acceptedAnswerCount) { this.acceptedAnswerCount = acceptedAnswerCount; }
    public long getVotesReceived() { return votesReceived; }
    public void setVotesReceived(long votesReceived) { this.votesReceived = votesReceived; }
    public long getQuestionViews() { return questionViews; }
    public void setQuestionViews(long questionViews) { this.questionViews = questionViews; }
    public long getBookmarkCount() { return bookmarkCount; }
    public void setBookmarkCount(long bookmarkCount) { this.bookmarkCount = bookmarkCount; }
    public long getBadgeCount() { return badgeCount; }
    public void setBadgeCount(long badgeCount) { this.badgeCount = badgeCount; }
}

