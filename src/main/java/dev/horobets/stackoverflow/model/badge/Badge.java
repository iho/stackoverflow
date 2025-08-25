package dev.horobets.stackoverflow.model.badge;

import dev.horobets.stackoverflow.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "badges", indexes = {
        @Index(name = "ix_badges_name", columnList = "name", unique = true)
})
public class Badge extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private BadgeType type;

    @Size(max = 1024)
    private String iconUrl;

    public Badge() {}

    public Badge(String name, BadgeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}
