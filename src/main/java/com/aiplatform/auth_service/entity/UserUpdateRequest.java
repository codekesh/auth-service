package com.aiplatform.auth_service.entity;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String phone;
}