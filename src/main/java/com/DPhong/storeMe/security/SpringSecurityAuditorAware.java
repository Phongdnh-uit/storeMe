package com.DPhong.storeMe.security;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

  private final SecurityUtils securityUtils;
  /**
   * @return the current auditor. 0L is used as a default value if no user is authenticated.
   */
  @Override
  public Optional<Long> getCurrentAuditor() {
    if (!securityUtils.isAuthenticated()) {
      return Optional.of(0L);
    }
    Long userId = securityUtils.getCurrentUserId();
    return Optional.ofNullable(userId);
  }
}
