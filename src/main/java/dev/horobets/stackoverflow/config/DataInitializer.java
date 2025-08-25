package dev.horobets.stackoverflow.config;

import dev.horobets.stackoverflow.model.user.Role;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }
        if (roleRepository.findByName(RoleName.ROLE_MODERATOR).isEmpty()) {
            roleRepository.save(new Role(RoleName.ROLE_MODERATOR));
        }
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }
}

