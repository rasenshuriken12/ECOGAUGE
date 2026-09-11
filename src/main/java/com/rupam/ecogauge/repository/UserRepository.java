package com.rupam.ecogauge.repository;

import com.rupam.ecogauge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email The email to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByEmail(String email); //

    /**
     * Finds a user by their password reset token.
     * This is required for the password reset feature.
     * @param resetToken The token to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByResetToken(String resetToken);
}