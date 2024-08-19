package com.translate.app.service.impl;

import com.translate.app.dao.entity.Client;
import com.translate.app.dao.entity.PasswordResetToken;
import com.translate.app.dao.entity.User;
import com.translate.app.dao.repository.ClientRepository;
import com.translate.app.dao.repository.PasswordResetTokenRepository;
import com.translate.app.dao.repository.UserRepository;
import com.translate.app.model.exception.InvalidTokenException;
import com.translate.app.model.exception.UserNotAssociatedException;
import com.translate.app.model.exception.UserNotFoundException;
import com.translate.app.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found with email: " + email));

        Optional<User> user = userRepository.findByUsername(client.getUsername());
        if (user.isEmpty()) {
            throw new UserNotAssociatedException("User not associated with client: " + email);
        }

        // Delete any existing token for the user
        tokenRepository.deleteByUser_Username(user.get().getUsername());

        // Generate and save a new token with expiration date
        String token = generateAndSaveActivationToken(client.getEmail());

        // Send the reset email
        sendResetEmail(client.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, take this token:\n" +token);

        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token has expired");
        }

        User user = userRepository.findByUsername(resetToken.getUser().getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Optionally, delete the token after use
        tokenRepository.deleteByToken(token);
    }


    private String generateAndSaveActivationToken(String email) {
        String generatedToken = generateActivationCode();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(generatedToken)
                .expirationDate(LocalDateTime.now().plusMinutes(15))  // Set expiration date here
                .user(clientRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found!")).getUser())
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
