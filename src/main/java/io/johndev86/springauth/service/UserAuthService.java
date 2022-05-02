package io.johndev86.springauth.service;

import io.johndev86.springauth.payload.JwtResponse;
import io.johndev86.springauth.payload.MessageResponse;


import java.util.Set;

public interface UserAuthService {
    JwtResponse login(String username, String password);

    MessageResponse register(String username, String password, String email, Set<String> roles);

}
