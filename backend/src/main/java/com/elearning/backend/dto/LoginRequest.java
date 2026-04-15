package com.elearning.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    // No validation annotations — just accept what comes in
    private String email;
    private String password;
}