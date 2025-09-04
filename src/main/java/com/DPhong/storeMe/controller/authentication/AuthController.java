package com.DPhong.storeMe.controller.authentication;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RefreshTokenRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.authentication.ResetPasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.SendVerifyEmailRequestDTO;
import com.DPhong.storeMe.dto.authentication.TwoFAChallengeResponseDTO;
import com.DPhong.storeMe.dto.authentication.UpdateAccountRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.service.authentication.AuthService;
import com.DPhong.storeMe.service.authentication.BlacklistTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoint xác thực người dùng")
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final AuthService authService;
  private final BlacklistTokenService blacklistTokenService;

  @Operation(summary = "Đăng ký tài khoản")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
      @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(ApiResponse.success(authService.registerUser(registerRequestDTO)));
  }

  @Operation(summary = "Đăng nhập tài khoản")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Object>> login(
      @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    Object response = authService.login(loginRequestDTO);
    if (response instanceof TwoFAChallengeResponseDTO) {
      return ResponseEntity.ok(ApiResponse.success(response, "2fa_required"));
    }
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "Làm mới token")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
      @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    return ResponseEntity.ok(
        ApiResponse.success(authService.refreshAccessToken(refreshTokenRequestDTO)));
  }

  @Operation(summary = "Gửi lại email xác thực tài khoản")
  @PostMapping("/registration/send-email")
  public ResponseEntity<ApiResponse<Void>> sendEmail(
      @Valid @RequestBody SendVerifyEmailRequestDTO request) {
    authService.resendVerifyEmail(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Xác thực tài khoản")
  @GetMapping("/verify-email")
  public ResponseEntity<ApiResponse<Void>> verifyEmail(
      @RequestParam("userId") Long userId, @RequestParam("code") String code) {
    authService.verifyEmail(userId, code);
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
  public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam("email") String email) {
    authService.sendForgotPasswordEmail(email);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Đặt lại mật khẩu sau khi quên")
  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
    authService.resetPassword(resetPasswordRequestDTO);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Đăng xuất tài khoản")
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader("Authorization") String token,
      @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    authService.logout(refreshTokenRequestDTO);
    if (token != null && token.startsWith("Bearer ")) {
      String accessToken = token.substring(7);
      blacklistTokenService.addToBlacklist(accessToken);
    } else throw new IllegalArgumentException("Invalid token format");
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Lấy thông tin người dùng hiện tại")
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser() {
    return ResponseEntity.ok(ApiResponse.success(authService.getAccount()));
  }

  @Operation(summary = "Cập nhật thông tin người dùng hiện tại")
  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserResponseDTO>> updateAccount(
      @Valid @RequestBody UpdateAccountRequestDTO userResponseDTO) {
    return ResponseEntity.ok(ApiResponse.success(authService.updateAccount(userResponseDTO)));
  }
}
