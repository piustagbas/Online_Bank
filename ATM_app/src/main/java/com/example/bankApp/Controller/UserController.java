package com.example.bankApp.Controller;

import com.example.bankApp.Config.JwtTokenUtil;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.UserRepository;
import com.example.bankApp.ServiceImp.AuthTokenServiceImpl;
import com.example.bankApp.ServiceImp.UserServiceImpl;
import com.example.bankApp.dto.JwtResponse;
import com.example.bankApp.dto.LoginRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserServiceImpl userService;
    @Autowired
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private final AuthTokenServiceImpl authTokenService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();

        ResponseEntity<?> register = userService.registerUser(username, password);
        if (register.getStatusCode() != HttpStatus.CREATED) {
            return register;
        }
        boolean emailSent = false;
        int retryAttempts = 0;
            while (!emailSent && retryAttempts < MAX_RETRY_ATTEMPTS) {
                try {
                    userService.email(request.getUsername());
                    emailSent = true;
                } catch (MessagingException e) {
                    new RuntimeException("Email not sent");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    retryAttempts++;
                }
            }
            if (emailSent) {
                return ResponseEntity.ok("Registration successful. Please check your email for further instructions.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send registration email after multiple trials.");
            }
        }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Attempt to authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // User is authenticated; generate a JWT token
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            System.out.println(token);
            // Create a new token and save it in the database
            Users user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginRequest.getUsername()));

            authTokenService.createToken(user, token, jwtTokenUtil.getExpirationDateFromToken(token));

            // Return the token in a JwtResponse object
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            // Authentication failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtTokenUtil.extractTokenFromRequest(request);
        System.out.println(token);
        if (token == null || !jwtTokenUtil.validateToken(token) || authTokenService.isTokenRevoked(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Users user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        authTokenService.revokeToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}