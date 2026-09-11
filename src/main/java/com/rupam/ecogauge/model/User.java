package com.rupam.ecogauge.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.Instant; // <-- NEW IMPORT

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String password; // Nullable for OAuth2 users

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL; // Default provider

    @Column(nullable = false)
    private String role = "USER"; // Default role is USER

    // --- NEW FIELDS FOR PASSWORD RESET ---
    private String resetToken;
    private Instant resetTokenExpiry;
}