package io.johndev86.springauth.service;

import io.johndev86.springauth.model.User;
import io.johndev86.springauth.payload.JwtResponse;
import io.johndev86.springauth.payload.MessageResponse;
import io.johndev86.springauth.respository.RoleRepository;
import io.johndev86.springauth.respository.UserRepository;
import io.johndev86.springauth.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserAuthServiceImplIT {


    @Autowired
    UserAuthService userAuthService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;


    private String USER_NAME = "test";
    private String PASSWORD = "123456";
    private String EMAIL = "test@test.com";

    private String NEW_USER_NAME = "test3";
    private String NEW_PASSWORD = "123456";
    private String NEW_EMAIL = "test3@test.com";

    private String SUCCESS_RESPONSE = "User registered successfully!";


    @Transactional
    @Test
    void login() {

        if (!userRepository.existsByUsername(USER_NAME)) {
            userRepository.save(new User(USER_NAME, EMAIL, encoder.encode(PASSWORD)));
        }
        JwtResponse jwtResponse = userAuthService.login(USER_NAME, PASSWORD);

        assertEquals(jwtResponse.getEmail(), EMAIL);
        assertEquals(jwtResponse.getUsername(), USER_NAME);
    }

    @Transactional
    @Test
    void register() {
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        roles.add("user");

        MessageResponse response = userAuthService.register(NEW_USER_NAME, NEW_PASSWORD, NEW_EMAIL, roles);

        assertEquals(response.getMessage(), SUCCESS_RESPONSE);

        JwtResponse jwtResponse = userAuthService.login(NEW_USER_NAME, NEW_PASSWORD);

        assertEquals(jwtResponse.getEmail(), NEW_EMAIL);
        assertEquals(jwtResponse.getUsername(), NEW_USER_NAME);
    }
}