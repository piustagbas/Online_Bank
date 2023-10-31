package com.example.bankApp.ServiceImp;

import com.example.bankApp.Model.AuthToken;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.AuthTokenRepository;
import com.example.bankApp.Service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Override
    public AuthToken createToken(Users user, String token, Date expirationDate) {
        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.setToken(token);
        authToken.setExpirationDate(expirationDate);
        authToken.setRevoked(false);
        return authTokenRepository.save(authToken);
    }
    @Override
    public boolean isTokenRevoked(String token) {
        AuthToken authToken = authTokenRepository.findByToken(token);
        return authToken == null || authToken.isRevoked();
    }
    @Override
    public void revokeToken(String tokenValue) {
        AuthToken authToken = authTokenRepository.findByToken(tokenValue);
        if (authToken != null) {
            authToken.setRevoked(true);
            authTokenRepository.save(authToken);
        }
    }
}

