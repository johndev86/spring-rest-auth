package io.johndev86.springauth.service;

import io.johndev86.springauth.exception.TokenRefreshException;
import io.johndev86.springauth.model.RefreshToken;
import io.johndev86.springauth.model.User;
import io.johndev86.springauth.payload.TokenRefreshResponse;
import io.johndev86.springauth.respository.RefreshTokenRepository;
import io.johndev86.springauth.respository.UserRepository;
import io.johndev86.springauth.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.text.html.Option;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenService;

    private Long refreshTokenDurationMs = 86400000L;

    private Long USER_ID;
    private User USER;
    private String TOKEN;
    private Instant EXPIRY;
    private RefreshToken REFRESH_TOKEN;

    private String EXPIRY_MESSAGE = "Refresh token was expired. Please make a new signin request";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        USER_ID = 1L;
        USER = new User(USER_ID.toString(), "test@test.com", "123456");
        TOKEN = "testtoken";
        EXPIRY = Instant.now().plusMillis(refreshTokenDurationMs);
        REFRESH_TOKEN = new RefreshToken(USER_ID, USER, TOKEN, EXPIRY);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshTokenDurationMs);
    }

    @Test
    void findByToken() {

        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(REFRESH_TOKEN));

        Optional<RefreshToken> token = refreshTokenService.findByToken(TOKEN);

        verify(refreshTokenRepository, times(1)).findByToken(TOKEN);
        assertEquals(token.get().getToken(), TOKEN);
    }

    @Test
    void createRefreshToken() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(REFRESH_TOKEN);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        assertEquals(refreshToken.getToken(), TOKEN);
        assertNotNull(refreshToken.getId());
    }

    @Test
    void verifyExpiration() {
        Instant past = Instant.now().minusMillis(100);
        REFRESH_TOKEN.setExpiryDate(past);

        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(REFRESH_TOKEN), EXPIRY_MESSAGE);

        verify(refreshTokenRepository, times(1)).delete(any(RefreshToken.class));

    }

    @Test
    void refreshAccessToken() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(REFRESH_TOKEN));
        when(jwtUtils.generateTokenFromUsername(anyString())).thenReturn(TOKEN);

        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshAccessToken(REFRESH_TOKEN.getToken());

        verify(refreshTokenRepository, times(1)).findByToken(anyString());
        assertNotNull(tokenRefreshResponse.getAccessToken());
        assertEquals(tokenRefreshResponse.getRefreshToken(), REFRESH_TOKEN.getToken());
    }

    @Test
    void deleteByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER));
        when(refreshTokenRepository.deleteByUser(any(User.class))).thenReturn(1);

        refreshTokenService.deleteByUserId(USER_ID);

        verify(userRepository, times(1)).findById(anyLong());
        verify(refreshTokenRepository, times(1)).deleteByUser(any(User.class));
    }
}