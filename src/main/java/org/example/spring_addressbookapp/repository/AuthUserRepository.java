package org.example.spring_addressbookapp.repository;

import org.example.spring_addressbookapp.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findByResetToken(String resetToken);
}
