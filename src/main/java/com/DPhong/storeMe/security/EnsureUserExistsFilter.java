package com.DPhong.storeMe.security;

import com.DPhong.storeMe.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class EnsureUserExistsFilter extends OncePerRequestFilter {

  private final SecurityUtils securityUtils;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (securityUtils.isRealUserAuthenticated()) {
      Long userId = securityUtils.getCurrentUserId();
      if (userId != null && !userRepository.existsById(userId)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
