package com.DPhong.storeMe.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityUtils {

  /**
   * @return the ID of the currently authenticated user, or null if no user is authenticated
   */
  public static Long getCurrentUserId() {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null || context.getAuthentication() == null) {
      throw new IllegalStateException("No authentication information found");
    }
    Object principal = context.getAuthentication().getPrincipal();
    if (principal instanceof UserDetails userDetails) {
      return ((CustomUserDetails) userDetails).getId();
    } else if (principal instanceof Jwt jwt) {
      return Long.valueOf(jwt.getSubject());
    } else if (principal instanceof String) {
      // This is a fallback case, where user is enter permitted endpoint without authentication
      return null;
    }
    throw new IllegalStateException("Unknown principal type: " + principal.getClass());
  }

  /**
   * @return the username of the currently authenticated user, or null if no user is authenticated
   */
  public static boolean isAuthenticated() {
    SecurityContext context = SecurityContextHolder.getContext();
    return context != null
        && context.getAuthentication() != null
        && context.getAuthentication().isAuthenticated();
  }
}
