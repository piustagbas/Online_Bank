package com.example.bankApp.Repository;

import com.example.bankApp.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
    Optional<Users> findByUsername(String username);
    Optional<Users> findByUsernameIgnoreCase(String username);
    Optional<Users> findById(Long id);
}
