package io.johndev86.springauth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.johndev86.springauth.payload.JwtResponse;
import io.johndev86.springauth.payload.LoginRequest;
import io.johndev86.springauth.payload.MessageResponse;
import io.johndev86.springauth.payload.SignupRequest;
import io.johndev86.springauth.service.UserAuthService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    UserAuthService authService;

    @InjectMocks
    AuthController authController;

    MockMvc mockMvc;

    private String USER_NAME = "test";
    private String PASSWORD = "123456";
    private String EMAIL = "test@test.com";
    private String TOKEN = "ABCD";
    private List<String> ROLES_RES = new ArrayList<>();
    private Set<String> ROLES_REQ = new HashSet<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private String SUCCESS_RESPONSE = "User registered successfully!";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .build();

        ROLES_RES.add("admin");
        ROLES_RES.add("user");

        ROLES_REQ.add("user");
    }

    @Test
    void authenticateUser() throws Exception {
        JwtResponse jwtResponse = new JwtResponse(TOKEN, "",1L, USER_NAME, EMAIL, ROLES_RES);

        when(authService.login(anyString(), anyString())).thenReturn(jwtResponse);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(USER_NAME);
        loginRequest.setPassword(PASSWORD);

        mockMvc.perform(post("/api/auth/signin").content(objectMapper.writeValueAsString(loginRequest)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", equalTo(TOKEN)))
                .andExpect(jsonPath("$.username", equalTo(USER_NAME)))
                .andExpect(jsonPath("$.email", equalTo(EMAIL)));
    }

    @Test
    void registerUser() throws Exception {
        MessageResponse messageResponse = new MessageResponse(SUCCESS_RESPONSE);

        Object Set;
        when(authService.register(anyString(), anyString(), anyString(), anySet())).thenReturn(messageResponse);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USER_NAME);
        signupRequest.setPassword(PASSWORD);
        signupRequest.setEmail(EMAIL);
        signupRequest.setRoles(ROLES_REQ);


        mockMvc.perform(post("/api/auth/signup").content(objectMapper.writeValueAsString(signupRequest)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo(SUCCESS_RESPONSE)));
    }


}