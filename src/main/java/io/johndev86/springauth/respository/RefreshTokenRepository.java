package io.johndev86.springauth.respository;

import io.johndev86.springauth.model.RefreshToken;
import io.johndev86.springauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    @Override
    Optional<RefreshToken> findById(Long id);
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
}
