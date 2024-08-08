package com.translate.app.service;

import com.translate.app.entity.Client;
import com.translate.app.entity.PasswordResetToken;
import com.translate.app.entity.User;
import com.translate.app.repository.ClientRepository;
import com.translate.app.repository.PasswordResetTokenRepository;
import com.translate.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int EXPIRATION_TIME_IN_MINUTES = 15;

    @Transactional
    public void initiatePasswordReset(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found with email: " + email));

        Optional<User> user = userRepository.findByUsername(client.getUsername());
        if (user.isEmpty()) {
            throw new RuntimeException("User not associated with client: " + email);
        }

        // Delete any existing token for the user
        tokenRepository.deleteByUsername(user.get().getUsername());

        // Generate a new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES);

        PasswordResetToken resetToken = new PasswordResetToken(token, user.get().getUsername(), expirationDate);
        tokenRepository.save(resetToken);

        // Send the reset email
        String resetLink = "https://localhost:9090/reset-password?token=" + token; // Don't have domain
        sendResetEmail(client.getEmail(), resetLink);
    }

    private void sendResetEmail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetLink);

        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = userRepository.findByUsername(resetToken.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Optionally, delete the token after use
        tokenRepository.deleteByToken(token);
    }
}
