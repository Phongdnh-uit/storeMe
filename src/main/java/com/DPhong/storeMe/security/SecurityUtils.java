package com.DPhong.storeMe.security;

import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * @return the ID of the currently authenticated user, or null if no user is authenticated
     */
    public Long getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new IllegalStateException("No authentication information found");
        }
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return ((CustomUserDetails) userDetails).getId();
        }
        if (principal instanceof Jwt jwt) {
            return Long.valueOf(jwt.getSubject());
        }
        if (principal instanceof String) {
            // This is a fallback case, where user is enter permitted endpoint without authentication
            return null;
        }
        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email == null) {
                throw new IllegalStateException("No email found in OAuth2 user attributes");
            }
            Optional<User> user = userRepository.findByEmail(email);
            return user.map(User::getId).orElse(null);
        }
        throw new IllegalStateException("Unknown principal type: " + principal.getClass());
    }

    /**
     * @return the username of the currently authenticated user, or null if no user is authenticated
     */
    public boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null
                && context.getAuthentication() != null
                && context.getAuthentication().isAuthenticated();
    }
}
