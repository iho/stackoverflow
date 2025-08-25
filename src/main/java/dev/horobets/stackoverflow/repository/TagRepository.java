package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.tag.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);

    @Query("select t from Tag t where lower(t.name) in :names")
    List<Tag> findAllByLowerNames(@Param("names") Collection<String> lowerNames);
}

