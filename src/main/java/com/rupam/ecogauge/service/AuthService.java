package com.rupam.ecogauge.service;

import com.rupam.ecogauge.dto.LoginRequest;
import com.rupam.ecogauge.dto.RegisterRequest;
import com.rupam.ecogauge.model.AuthProvider;
import com.rupam.ecogauge.model.User;
import com.rupam.ecogauge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage; // <-- NEW IMPORT
import org.springframework.mail.javamail.JavaMailSender; // <-- NEW IMPORT

import java.time.Instant; // <-- NEW IMPORT
import java.util.UUID; // <-- NEW IMPORT

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender; // <-- NEW INJECTION

    public User register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email address is already in use.");
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER"); // Set default role for new local users
        return userRepository.save(user);
    }

    public User login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));
        if (user.getPassword() != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Invalid email or password.");
        }
    }

    // --- NEW METHOD 1: REQUEST RESET ---
    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email address."));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new RuntimeException("This account is registered with Google. Please reset your password through Google.");
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(Instant.now().plusSeconds(3600)); // 1 hour expiry
        userRepository.save(user);

        // Send the email
        String resetUrl = "http://localhost:8080/reset-password.html?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@ecogauge.com"); // Can be anything
        message.setTo(user.getEmail());
        message.setSubject("EcoGauge Password Reset Request");
        message.setText("To reset your password, please click the link below:\n\n" + resetUrl + "\n\nThis link will expire in 1 hour.");

        mailSender.send(message);
    }

    // --- NEW METHOD 2: PERFORM RESET ---
    public void performReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token) // <-- You need to add this method in UserRepository
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));

        if (user.getResetTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Invalid or expired reset token.");
        }

        // Reset is successful
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear the token
        user.setResetTokenExpiry(null); // Clear the expiry
        userRepository.save(user);
    }
}