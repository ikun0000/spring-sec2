package com.example.springsec2.controller;

import com.example.springsec2.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {
    private final TokenService tokenService;


    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    private Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        String token = tokenService.generateToken(username, password);
        if (token != null) {
            return Map.of("code", 0, "token", token);
        } else {
            return Map.of("code", -3, "msg", "password invalid!");
        }
    }
}
