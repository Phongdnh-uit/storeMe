package com.DPhong.storeMe.interceptor;

import com.DPhong.storeMe.entity.RolePermission;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.repository.PermissionRepository;
import com.DPhong.storeMe.repository.RolePermissionRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class PermissionInterceptor implements HandlerInterceptor {
  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;
  private final RolePermissionRepository rolePermissionRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String path = request.getRequestURI();
    String method = request.getMethod();
    return hasPermission(path, method);
  }

  private boolean hasPermission(String path, String method) {
    Long userId = SecurityUtils.getCurrentUserId();
    if (userId == null) {
      return false;
    }
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    if (user.getRole().getName().equals("" + RoleName.SUPER_ADMIN)) {
      return true;
    }
    List<RolePermission> rolePermission =
        rolePermissionRepository.findAll(
            (root, _, builder) ->
                builder.and(builder.equal(root.get("roleId"), user.getRole().getId())));
    List<Long> permissionIds =
        rolePermission.stream().map(RolePermission::getPermissionId).toList();
    boolean hasPermission =
        permissionRepository.exists(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("path"), path),
                    builder.equal(root.get("method"), method),
                    root.get("id").in(permissionIds)));
    return hasPermission;
  }
}
