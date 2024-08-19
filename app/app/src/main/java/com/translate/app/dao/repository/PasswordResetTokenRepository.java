package com.translate.app.dao.repository;

import com.translate.app.dao.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser_Username(String username);
    void deleteByToken(String token);

}
