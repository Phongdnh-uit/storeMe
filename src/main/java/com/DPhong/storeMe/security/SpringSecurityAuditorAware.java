package com.DPhong.storeMe.security;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

  /**
   * @return the current auditor. 0L is used as a default value if no user is authenticated.
   */
  @Override
  public Optional<Long> getCurrentAuditor() {
    if (!SecurityUtils.isAuthenticated()) {
      return Optional.of(0L);
    }
    Long userId = SecurityUtils.getCurrentUserId();
    return Optional.ofNullable(userId);
  }
}
