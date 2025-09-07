package com.DPhong.storeMe.security;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

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
  public static CustomUserDetails getCurrentUserDetails() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;
    Object principal = auth.getPrincipal();
    if (principal instanceof CustomUserDetails userDetails) {
      return userDetails;
    }
    return null;
  }

  public static String getClientIp(HttpServletRequest request) {
    String clientIp = request.getHeader("X-Forwarded-For");
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("WL-Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getRemoteAddr();
    }
    // In case of multiple IPs, take the first one
    if (clientIp != null && clientIp.contains(",")) {
      clientIp = clientIp.split(",")[0].trim();
    }
    return clientIp;
  }

  public static String generateRandomNumber() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    String timestamp = now.format(formatter);
    int randomSuffix = new SecureRandom().nextInt(1000, 9999);
    return timestamp + randomSuffix;
  }
}
