package io.johndev86.springauth.bootstrap;

import io.johndev86.springauth.model.ERole;
import io.johndev86.springauth.model.Role;
import io.johndev86.springauth.respository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public Bootstrap(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty())
            roleRepository.save(Role.builder().name(ERole.ROLE_USER).build());
        if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty())
            roleRepository.save(Role.builder().name(ERole.ROLE_MODERATOR).build());
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty())
            roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build());

    }
}
