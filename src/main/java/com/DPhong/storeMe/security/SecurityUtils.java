package com.DPhong.storeMe.security;

import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

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

  /** return if user is authenticated, accept anonymousUser */
  public boolean isAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.isAuthenticated();
  }

  public boolean isRealUserAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return false;
    return !(auth.getPrincipal() instanceof String s && s.equals("anonymousUser"));
  }
}
