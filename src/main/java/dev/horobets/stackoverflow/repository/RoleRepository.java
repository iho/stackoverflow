package dev.horobets.stackoverflow.repository;

import dev.horobets.stackoverflow.model.user.Role;
import dev.horobets.stackoverflow.model.user.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

