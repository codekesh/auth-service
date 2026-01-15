package com.aiplatform.auth_service.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String register(String email, String password) {
        return "dummy-token";
    }

    public String login(String email, String password) {
        return "dummy-token";
    }
}
