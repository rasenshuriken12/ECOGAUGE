// File: src/main/java/com/rupam/ecogauge/security/SecurityConfig.java

package com.rupam.ecogauge.security;

import com.rupam.ecogauge.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <<<--- ENSURE THIS IMPORT IS PRESENT
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler; // Autowire the handler

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .disable() // Disable CSRF
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .disable() // Required for H2 console
                        )
                )
                .authorizeHttpRequests(authz -> authz
                        // --- PUBLIC ACCESS (UPDATED) ---
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/"), // Now public
                                AntPathRequestMatcher.antMatcher("/landing_page.html"), // Now public
                                AntPathRequestMatcher.antMatcher("/login.html"),
                                AntPathRequestMatcher.antMatcher("/signup.html"),
                                AntPathRequestMatcher.antMatcher("/google-auth.html"),
                                AntPathRequestMatcher.antMatcher("/error"),
                                AntPathRequestMatcher.antMatcher("/h2-console/**"),
                                AntPathRequestMatcher.antMatcher("/api/auth/**"), // Covers login, register, and password reset APIs
                                AntPathRequestMatcher.antMatcher("/request-reset.html"),
                                AntPathRequestMatcher.antMatcher("/reset-password.html"),
                                AntPathRequestMatcher.antMatcher("/static/**"),
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                AntPathRequestMatcher.antMatcher("/js/**"),
                                AntPathRequestMatcher.antMatcher("/*.html") // Allow access to other root html files
                        ).permitAll()

                        // --- USER/AUTHENTICATED ACCESS (UPDATED) ---
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/home"), // New authenticated home
                                AntPathRequestMatcher.antMatcher("/AQI_Home.html"), // Secured file
                                AntPathRequestMatcher.antMatcher("/noise"),
                                AntPathRequestMatcher.antMatcher("/maps/AQI_MAP.html"),
                                AntPathRequestMatcher.antMatcher("/maps/NOISE_MAP.html"),
                                AntPathRequestMatcher.antMatcher("/user_ranking.html"),
                                AntPathRequestMatcher.antMatcher("/feedback.html"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/stations"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/user/me")
                        ).authenticated()

                        // --- SPECIFIC POST FOR FEEDBACK (Authenticated users) ---
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/feedback") // Allow authenticated users to POST feedback
                        ).authenticated()


                        // --- ADMIN ACCESS (Unchanged) ---
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/dashboard"), // Admin dashboard page
                                AntPathRequestMatcher.antMatcher("/ranking.html"), // Admin ranking page
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/stations"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.PUT, "/api/stations/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/api/stations/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/feedback")
                        ).hasRole("ADMIN")

                        // Default catch-all rule: Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // --- LOGIN CONFIGURATION (Unchanged) ---
                .formLogin(form -> form // Local Form Login config
                        .loginPage("/login.html")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(authenticationSuccessHandler) // Use custom handler for role-based redirect
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 Config (Google)
                        .loginPage("/google-auth.html")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauthUserService)
                        )
                        .successHandler(authenticationSuccessHandler) // Use same custom handler
                )
                // --- LOGOUT CONFIGURATION (Unchanged) ---
                .logout(logout -> logout
                        .logoutSuccessUrl("/login.html?logout") // Redirect after logout
                        .permitAll()
                );

        return http.build();
    }
}