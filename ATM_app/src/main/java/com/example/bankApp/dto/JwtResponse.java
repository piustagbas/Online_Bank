package com.example.bankApp.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {

    }
}
