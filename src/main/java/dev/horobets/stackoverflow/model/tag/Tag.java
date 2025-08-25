package dev.horobets.stackoverflow.model.tag;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.post.Question;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "ix_tags_name", columnList = "name", unique = true)
})
public class Tag extends BaseEntity {

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "question_count", nullable = false)
    private int questionCount = 0;

    @ManyToMany(mappedBy = "tags")
    private Set<Question> questions = new HashSet<>();

    public Tag() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public Set<Question> getQuestions() { return questions; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }
}
