package com.rupam.ecogauge.controller;

import com.rupam.ecogauge.model.User;
import com.rupam.ecogauge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        // Get the email (which is the principal's name) from the authentication object
        String email = authentication.getName();

        // Find the user in the repository to get their full name
        Optional<User> userOptional = userRepository.findByEmail(email); //

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOptional.get();

        // Return a simple map with just the name and email (to avoid sending the password)
        return ResponseEntity.ok(Map.of(
                "name", user.getName(), //
                "email", user.getEmail() //
        ));
    }
}