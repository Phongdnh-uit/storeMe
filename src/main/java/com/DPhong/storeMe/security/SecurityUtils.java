package com.DPhong.storeMe.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityUtils {

  /**
   * @return the ID of the currently authenticated user, or null if no user is authenticated
   */
  public static Long getCurrentUserId() {
    CustomUserDetails userDetails = getCurrentUserDetails();
    return userDetails != null ? userDetails.getId() : null;
  }

  public static boolean isRealUserAuthenticated() {
    return getCurrentUserDetails() != null;
  }

  // ============================ HELPER METHOD ============================
  private static CustomUserDetails getCurrentUserDetails() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;
    Object principal = auth.getPrincipal();
    if (principal instanceof CustomUserDetails userDetails) {
      return userDetails;
    }
    return null;
  }
}
