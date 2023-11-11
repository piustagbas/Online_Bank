package com.example.bankApp.Service;

import com.example.bankApp.Model.Users;
import com.example.bankApp.dto.LoginRequest;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> registerUser(String username, String password) throws Exception;

    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    Users getUserById(Long userId);

    Users email(String email) throws MessagingException;
    void registerFcmToken(Long userId, String fcmToken);

}
