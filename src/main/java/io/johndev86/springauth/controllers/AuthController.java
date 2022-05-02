package io.johndev86.springauth.controllers;

import io.johndev86.springauth.payload.JwtResponse;
import io.johndev86.springauth.payload.LoginRequest;
import io.johndev86.springauth.payload.MessageResponse;
import io.johndev86.springauth.payload.SignupRequest;
import io.johndev86.springauth.respository.RoleRepository;
import io.johndev86.springauth.respository.UserRepository;
import io.johndev86.springauth.security.UserDetailsImpl;
import io.johndev86.springauth.security.jwt.JwtUtils;
import io.johndev86.springauth.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userAuthService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse messageResponse;
        try {
            messageResponse = userAuthService.register(signupRequest.getUsername(), signupRequest.getPassword(), signupRequest.getEmail(), signupRequest.getRoles());
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(messageResponse);
    }

}
