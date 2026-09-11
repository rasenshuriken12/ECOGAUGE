// In src/main/java/com/rupam/ecogauge/security/CustomAuthenticationSuccessHandler.java

package com.rupam.ecogauge.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/home"; // Default redirect for USER (AQI_Home.html)

        // Check if the user has the ADMIN role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            // Spring Security prefixes roles with 'ROLE_'
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/dashboard"; // Specific redirect for ADMIN
                break;
            }
        }

        // Redirect to the determined URL
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}