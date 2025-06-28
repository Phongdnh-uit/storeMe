package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RefreshTokenRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.service.authentication.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoint xác thực người dùng")
@RequestMapping(AppConstant.BASE_URL + "/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final AuthService authService;

  @Operation(summary = "Đăng ký tài khoản")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
      @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(ApiResponse.success(authService.registerUser(registerRequestDTO)));
  }

  @Operation(summary = "Đăng nhập tài khoản")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
      @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    return ResponseEntity.ok(ApiResponse.success(authService.login(loginRequestDTO)));
  }

  @Operation(summary = "Làm mới token")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
      @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    return ResponseEntity.ok(
        ApiResponse.success(authService.refreshAccessToken(refreshTokenRequestDTO)));
  }

  @Operation(summary = "Xác thực tài khoản")
  @GetMapping("/verify-email")
  public String verifyEmail(
      @RequestParam("userId") Long userId, @RequestParam("code") String code) {
    authService.verifyEmail(userId, code);
    return "verify-email";
  }

  @Operation(summary = "Gửi lại email xác thực tài khoản")
  @PostMapping("/registration/{userId}/send-email")
  public ResponseEntity<ApiResponse<Void>> sendEmail(@PathVariable("userId") Long userId) {
    authService.resendVerifyEmail(userId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Đặt lại mật khẩu")
  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
    authService.changePassword(changePasswordRequestDTO);
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
  public ResponseEntity<ApiResponse<Void>> logout(
      @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    authService.logout(refreshTokenRequestDTO);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser() {
    return ResponseEntity.ok(ApiResponse.success(authService.getAccount()));
  }
}
