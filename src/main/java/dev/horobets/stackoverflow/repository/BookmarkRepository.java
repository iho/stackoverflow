package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    long countByUser_Id(Long userId);
}

