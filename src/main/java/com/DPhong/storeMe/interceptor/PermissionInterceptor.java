package com.DPhong.storeMe.interceptor;

import com.DPhong.storeMe.repository.PermissionRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class PermissionInterceptor implements HandlerInterceptor {

  private final SecurityUtils securityUtils;
  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String path = request.getRequestURI();
    String method = request.getMethod();
    return true;
  }

  private boolean hasPermission(String path, String method) {
    return true;
  }
}
