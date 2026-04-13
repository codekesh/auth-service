package com.aiplatform.auth_service.service;

import com.aiplatform.auth_service.entity.User;
import com.aiplatform.auth_service.entity.UserResponse;
import com.aiplatform.auth_service.entity.UserUpdateRequest;
import com.aiplatform.auth_service.repository.UserRepository;
import com.aiplatform.auth_service.security.JwtUtil;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String register(String name, String email, String phone, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(encoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);
        return jwtUtil.generateToken(email);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(email);
    }

    public UserResponse getCurrentUser(String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.validateTokenAndGetEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getPhone());
    }

    public UserResponse updateUser(String authHeader, UserUpdateRequest request) {

        String token = authHeader.substring(7);
        String email = jwtUtil.validateTokenAndGetEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);

        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getPhone());
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getName(),
                        user.getEmail(),
                        user.getPhone()))
                .toList();
    }
}
