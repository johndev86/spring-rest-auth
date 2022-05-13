package io.johndev86.springauth.service;

import io.johndev86.springauth.model.ERole;
import io.johndev86.springauth.model.Role;
import io.johndev86.springauth.model.User;
import io.johndev86.springauth.payload.JwtResponse;
import io.johndev86.springauth.payload.MessageResponse;
import io.johndev86.springauth.respository.RoleRepository;
import io.johndev86.springauth.respository.UserRepository;
import io.johndev86.springauth.security.UserDetailsImpl;
import io.johndev86.springauth.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private String TEST_TOKEN = "abcdef1234";
    private String USER_NAME = "test";
    private String PASSWORD = "123";
    private String ENCODED_PASSWORD = "abc";
    private String EMAIL = "test@test.com";
    private Long TEST_ID = 1L;

    private String SUCCESS_RESPONSE = "User registered successfully!";

    @InjectMocks
    UserAuthServiceImpl userAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1, ERole.ROLE_USER));
        roles.add(new Role(2, ERole.ROLE_MODERATOR));

        User user = new User(USER_NAME, EMAIL, PASSWORD);
        user.setRoles(roles);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any())).thenReturn(TEST_TOKEN);

        JwtResponse jwtResponse = userAuthService.login(USER_NAME, PASSWORD);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(userDetails);
        assertEquals(TEST_TOKEN, jwtResponse.getToken());
        assertEquals(USER_NAME, jwtResponse.getUsername());
    }

    @Test
    void register() {
        Role roleUser = new Role(1, ERole.ROLE_USER);
        Role roleMod = new Role(2, ERole.ROLE_MODERATOR);
        Set<String> strRoles = new HashSet<>(Arrays.asList("mod", "user"));
        Set<Role> roles = new HashSet<>();
        roles.add(roleMod);
        roles.add(roleUser);

        User newUser = new User(USER_NAME, EMAIL, ENCODED_PASSWORD);
        newUser.setRoles(roles);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(Optional.of(roleMod));

        when(encoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        MessageResponse response = userAuthService.register(USER_NAME, PASSWORD, EMAIL, strRoles);

        verify(userRepository, times(1)).save(newUser);
        assertEquals(SUCCESS_RESPONSE, response.getMessage());
    }
}