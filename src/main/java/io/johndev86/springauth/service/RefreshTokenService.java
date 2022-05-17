package io.johndev86.springauth.service;

import io.johndev86.springauth.model.RefreshToken;
import io.johndev86.springauth.payload.TokenRefreshResponse;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    TokenRefreshResponse refreshAccessToken(String refreshToken);

    int deleteByUserId(Long userId);
}
