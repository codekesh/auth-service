package com.aiplatform.auth_service.controller;

import com.aiplatform.auth_service.dto.AuthRequest;
import com.aiplatform.auth_service.dto.AuthResponse;
import com.aiplatform.auth_service.entity.UserResponse;
import com.aiplatform.auth_service.entity.UserUpdateRequest;
import com.aiplatform.auth_service.service.AuthService;
import com.aiplatform.auth_service.security.JwtUtil;

import java.util.List;

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
                authService.register(request.getName(), request.getEmail(), request.getPhone(), request.getPassword()));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return new AuthResponse(
                authService.login(request.getEmail(), request.getPassword()));
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

    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        return authService.getCurrentUser(authHeader);
    }

    @PutMapping("/me")
    public UserResponse updateUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserUpdateRequest request) {

        return authService.updateUser(authHeader, request);
    }

    @GetMapping("/admin/users")
    public List<UserResponse> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {

        // 🔥 Validate token
        String email = jwtUtil.validateTokenAndGetEmail(authHeader.substring(7));

        if (email == null) {
            throw new RuntimeException("Invalid token");
        }

        // ❗ TEMP: allow all (later restrict to admin)
        return authService.getAllUsers();
    }
}
