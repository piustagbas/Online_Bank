package com.example.bankApp.Repository;

import com.example.bankApp.Model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    AuthToken findByToken(String token);

    boolean existsByToken(String token);
}
