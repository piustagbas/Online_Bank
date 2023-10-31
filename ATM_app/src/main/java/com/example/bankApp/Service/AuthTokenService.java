package com.example.bankApp.Service;

import com.example.bankApp.Model.AuthToken;
import com.example.bankApp.Model.Users;

import java.util.Date;

public interface AuthTokenService {
    AuthToken createToken(Users user, String token, Date expirationDate);

    boolean isTokenRevoked(String token);

    void revokeToken(String token);
}
