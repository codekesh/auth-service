package com.aiplatform.auth_service.entity;

import lombok.*;

@Data
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private String phone;
}
