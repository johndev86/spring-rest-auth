package io.johndev86.springauth.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.johndev86.springauth.model.ERole;
import io.johndev86.springauth.model.User;
import io.johndev86.springauth.respository.UserRepository;
import io.johndev86.springauth.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    private Long ID = 1L;
    private String USERNAME = "test";
    private String EMAIL = "test@test.com";
    private String PASSWORD = "123";

    private List<GrantedAuthority> AUTHORITIES = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AUTHORITIES.add(new SimpleGrantedAuthority(ERole.ROLE_USER.name()));
        AUTHORITIES.add(new SimpleGrantedAuthority(ERole.ROLE_MODERATOR.name()));
    }

    @Test
    void loadUserByUsername() {
        User user = new User(USERNAME, EMAIL, PASSWORD);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetailsImpl userDetails = (UserDetailsImpl)userDetailsService.loadUserByUsername(USERNAME);

        assertEquals(userDetails.getUsername(), USERNAME);
        assertEquals(userDetails.getPassword(), PASSWORD);
        assertEquals(userDetails.getEmail(), EMAIL);
    }
}