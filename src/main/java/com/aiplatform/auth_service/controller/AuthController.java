package com.aiplatform.auth_service.controller;

import com.aiplatform.auth_service.dto.AuthRequest;
import com.aiplatform.auth_service.dto.AuthResponse;
import com.aiplatform.auth_service.service.AuthService;
import com.aiplatform.auth_service.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return new AuthResponse(
                authService.register(request.getEmail(), request.getPassword())
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return new AuthResponse(
                authService.login(request.getEmail(), request.getPassword())
        );
    }

    @GetMapping("/validate")
    public String validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (!authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.validateTokenAndGetEmail(token);

        if (email == null) {
            throw new RuntimeException("Invalid token");
        }

        return email;
    }
}
