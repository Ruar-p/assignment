package com.example.backend.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    // Password excluded for security
}
