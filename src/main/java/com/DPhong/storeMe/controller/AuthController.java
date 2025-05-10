package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.security.TokenProvider;
import com.DPhong.storeMe.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoint xác thực người dùng")
@RequestMapping(AppConstant.BASE_URL + "/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserService userService;
  private final TokenProvider tokenProvider;

  @Operation(summary = "Đăng ký tài khoản")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
      @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(
        ApiResponse.<UserResponseDTO>builder()
            .statusCode(HttpStatus.CREATED.value())
            .message("success")
            .data(userService.registerUser(registerRequestDTO))
            .build());
  }

  @Operation(summary = "Đăng nhập tài khoản")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
      @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = tokenProvider.generateAccessToken(SecurityUtils.getCurrentUserId());

    AuthResponseDTO authResponseDTO =
        new AuthResponseDTO().setAccessToken(accessToken).setRefreshToken(null);

    return ResponseEntity.ok(ApiResponse.success(authResponseDTO));
  }

  @Operation(summary = "Xác thực tài khoản")
  @PostMapping("/verify-email")
  public String verifyEmail() {
    return "verify-email";
  }

  @Operation(summary = "Đặt lại mật khẩu")
  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
    userService.changePassword(changePasswordRequestDTO);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Gửi email quên mật khẩu")
  @PostMapping("/forgot-password")
  public String forgotPassword() {
    return "forgot-password";
  }

  @Operation(summary = "Đặt lại mật khẩu sau khi quên")
  @PostMapping("/reset-password")
  public String resetPassword() {
    return "reset-password";
  }

  @Operation(summary = "Đăng xuất tài khoản")
  @PostMapping("/logout")
  public String logout() {
    return "logout";
  }
}
