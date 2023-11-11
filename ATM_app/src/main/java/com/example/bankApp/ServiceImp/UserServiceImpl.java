package com.example.bankApp.ServiceImp;

import com.example.bankApp.Config.JwtTokenUtil;
import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.UserRepository;
import com.example.bankApp.Service.UserService;
import com.example.bankApp.dto.JwtResponse;
import com.example.bankApp.dto.LoginRequest;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthTokenServiceImpl authTokenService;
    @Autowired
    private final EmailServiceImpl mailService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, AuthTokenServiceImpl authTokenService, EmailServiceImpl mailService) {
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
        this.mailService = mailService;
    }

    @Override
public ResponseEntity<?> registerUser(String username, String password) throws Exception {
    if (userRepository.findByUsername(username).isPresent()) {
        throw new Exception("Username already exists");
    }
    String hashedPassword = passwordEncoder.encode(password);
    Users user = new Users();
    user.setUsername(username);
    user.setPassword(hashedPassword);
    user.setFcmToken(user.getFcmToken());
    userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).body("User registration successful.");
}
        @Override
        public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
            try {
//
                // User is authenticated; generate a JWT token
                UserDetails userDetails = loadUserByUsername(loginRequest.getUsername());
                String token = jwtTokenUtil.generateToken(userDetails);

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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user details from your data store
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            // You can add user authorities to the list if needed
            List<GrantedAuthority> authorities = new ArrayList<>();
            // Add user authorities to the list

            return new User(user.get().getUsername(), user.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
@Override
    public Users getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    @Override
    public Users email(String email) throws MessagingException {
        Users users = new Users();
        users.setUsername(email);
        mailService.sendEmail(email, EmailServiceImpl.CONTENT, EmailServiceImpl.SUBJECT);
        return users;
    }

    @Override
    public void registerFcmToken(Long userId, String fcmToken) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

}
