package com.example.bankApp.Config;

import com.example.bankApp.Model.Users;
import com.example.bankApp.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${application.secretKey}")
    private String secret; // Secret key for signing and verifying the token
    @Value("${application.expiration}")
    private long expiration; // Token expiration time (in milliseconds)
//    byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
    private final UserRepository userRepository;

    public JwtTokenUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Generate a token based on user details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Create a token
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String jwtToken) {
        return
                Jwts.
                        parserBuilder()
                        .setSigningKey(getSignInKey())
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

    }

    private Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    // Extract username from the token
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    // Get claims from the token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // Validate whether the token is expired
    public boolean isTokenExpired(String jwtToken) {
        Date expirationDate = getExpirationDateFromToken(jwtToken);
        return expirationDate.before(new Date());
    }

    public boolean isTokenValidated(String jwtToken, UserDetails userDetails) {
        String username = extractUsername(jwtToken);
        return (userDetails.getUsername().equals(username) && !isTokenExpired(jwtToken));
    }

    // Get the expiration date from the token
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    // Validate if a token can be refreshed
    public boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    // Refresh a token
    public String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return createToken(claims, claims.getSubject());
    }

    // Validate if a token is valid
    public boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        UserDetails userDetails = loadUserByUsername(username);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UserDetails loadUserByUsername(String username) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            // Adjust authorities as needed for your use case
            List<GrantedAuthority> authorities = new ArrayList<>();
            // Add user authorities to the list

            return new User(user.get().getUsername(), user.get().getPassword(), authorities);
        } else {
            return null;
        }
    }
}

