package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RefreshTokenRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.RefreshToken;
import com.DPhong.storeMe.entity.Verification;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.enums.VerificationType;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.security.TokenProvider;
import com.DPhong.storeMe.service.authentication.RefreshTokenService;
import com.DPhong.storeMe.service.authentication.VerificationService;
import com.DPhong.storeMe.service.general.MailService;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoint xác thực người dùng")
@RequestMapping(AppConstant.BASE_URL + "/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserService userService;
  private final TokenProvider tokenProvider;
  private final VerificationService verificationService;
  private final RefreshTokenService refreshTokenService;
  private final MailService mailService;

  @Operation(summary = "Đăng ký tài khoản")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
      @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    UserResponseDTO userResponseDTO = userService.registerUser(registerRequestDTO);
    Verification verification =
        verificationService.createVerification(
            userResponseDTO.getId(), VerificationType.ACTIVATION);
    mailService.sendActivationEmail(
        userResponseDTO.getEmail(), userResponseDTO.getId(), verification.getCode());
    return ResponseEntity.ok(
        ApiResponse.<UserResponseDTO>builder()
            .statusCode(HttpStatus.CREATED.value())
            .message("success")
            .data(userResponseDTO)
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

    Long userId = SecurityUtils.getCurrentUserId();

    String accessToken = tokenProvider.generateAccessToken(userId);

    String refreshToken = refreshTokenService.generateRefreshToken(userId).getToken();

    AuthResponseDTO authResponseDTO =
        new AuthResponseDTO().setAccessToken(accessToken).setRefreshToken(refreshToken);

    return ResponseEntity.ok(ApiResponse.success(authResponseDTO));
  }

  @Operation(summary = "Làm mới token")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
      @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    RefreshToken refreshToken =
        refreshTokenService.verifyRefreshToken(refreshTokenRequestDTO.getRefreshToken());
    Long userId = refreshToken.getUser().getId();
    refreshTokenService.deleteRefreshToken(refreshToken);
    String accessToken = tokenProvider.generateAccessToken(userId);
    String newRefreshToken = refreshTokenService.generateRefreshToken(userId).getToken();

    AuthResponseDTO authResponseDTO =
        new AuthResponseDTO().setAccessToken(accessToken).setRefreshToken(newRefreshToken);

    return ResponseEntity.ok(ApiResponse.success(authResponseDTO));
  }

  @Operation(summary = "Xác thực tài khoản")
  @GetMapping("/verify-email")
  public String verifyEmail(
      @RequestParam("userId") Long userId, @RequestParam("code") String code) {
    Verification verification =
        verificationService.verifyCode(userId, code, VerificationType.ACTIVATION);
    verificationService.deleteVerification(verification);
    userService.updateStatus(userId, UserStatus.ACTIVE);
    return "verify-email";
  }

  @Operation(summary = "Gửi lại email xác thực tài khoản")
  @PostMapping("/registration/{userId}/send-email")
  public ResponseEntity<ApiResponse<Void>> sendEmail(@PathVariable("userId") Long userId) {
    Verification verification =
        verificationService.createVerification(userId, VerificationType.ACTIVATION);
    mailService.sendActivationEmail(
        verification.getUser().getEmail(), userId, verification.getCode());
    return ResponseEntity.ok(ApiResponse.success(null));
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
  public ResponseEntity<ApiResponse<Void>> logout(
      @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    RefreshToken refreshToken =
        refreshTokenService.findByToken(refreshTokenRequestDTO.getRefreshToken());
    refreshTokenService.deleteRefreshToken(refreshToken);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
