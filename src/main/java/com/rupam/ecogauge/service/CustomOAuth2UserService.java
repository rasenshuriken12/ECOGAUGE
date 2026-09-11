package com.rupam.ecogauge.service;

import com.rupam.ecogauge.model.AuthProvider;
import com.rupam.ecogauge.model.User;
import com.rupam.ecogauge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Fetch the user details from Google
        OAuth2User oauthUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");

        // 2. Find or create the user in your local database
        User user = processOAuth2User(email, attributes);

        // 3. Create the authorities (roles) for Spring Security
        // This ensures the user has the "ROLE_USER" authority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()) //
        );

        // 4. Return a new OAuth2User that uses "email" as the name attribute
        // This is the critical fix. It tells Spring `authentication.getName()` should return the email.
        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email" // Use the "email" attribute as the principal name
        );
    }

    private User processOAuth2User(String email, Map<String, Object> attributes) {
        Optional<User> userOptional = userRepository.findByEmail(email); //
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update user's name if it has changed on Google
            user.setName((String) attributes.get("name"));
        } else {
            // Register a new user
            user = new User();
            user.setProvider(AuthProvider.GOOGLE); //
            user.setEmail(email);
            user.setName((String) attributes.get("name"));
            user.setRole("USER"); // Set default role for new OAuth2 users
        }

        return userRepository.save(user);
    }
}