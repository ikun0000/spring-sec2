package com.example.springsec2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {
    private static final Map<String, String> tokens = new HashMap<>();

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public TokenService(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateToken(String username, String password) {
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return null;
        }
        if (userDetails == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return null;
        }

        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public UserDetails verifyToken(String token) {
        String name = tokens.get(token);
        if (name == null) {
            throw new UsernameNotFoundException("token is invalid!");
        }
        return userDetailsService.loadUserByUsername(name);
    }
}
