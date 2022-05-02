package io.johndev86.springauth.respository;

import io.johndev86.springauth.model.ERole;
import io.johndev86.springauth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
