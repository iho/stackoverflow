package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.badge.UserBadge;
import dev.horobets.stackoverflow.model.badge.UserBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {
    long countByUser_Id(Long userId);
}
