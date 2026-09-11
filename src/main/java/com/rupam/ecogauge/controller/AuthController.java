package com.rupam.ecogauge.controller;

import com.rupam.ecogauge.dto.LoginRequest;
import com.rupam.ecogauge.dto.RegisterRequest;
// --- NEW DTOs ---
import com.rupam.ecogauge.dto.PasswordResetRequest;
import com.rupam.ecogauge.dto.PasswordResetPerform;
// ---
import com.rupam.ecogauge.model.User;
import com.rupam.ecogauge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest); //
            return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Note: This API endpoint handles local login validation.
            // Spring Security takes care of establishing the session automatically upon successful validation.
            User user = authService.login(loginRequest); //
            return ResponseEntity.ok(Map.of("message", "Login successful!", "user", user.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // --- NEW ENDPOINT 1: REQUEST RESET ---
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            authService.generateResetToken(request.getEmail()); //
            return ResponseEntity.ok(Map.of("message", "A password reset link has been sent to your email."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- NEW ENDPOINT 2: PERFORM RESET ---
    @PostMapping("/perform-reset")
    public ResponseEntity<?> performPasswordReset(@RequestBody PasswordResetPerform request) {
        try {
            authService.performReset(request.getToken(), request.getNewPassword()); //
            return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}