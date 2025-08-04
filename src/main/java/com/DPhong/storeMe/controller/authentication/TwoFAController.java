package com.DPhong.storeMe.controller.authentication;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPLoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.TOTPSetupResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPVerifySetupResponseDTO;
import com.DPhong.storeMe.service.authentication.TwoFAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Two Factor Authentication", description = "Quản lý xác thực hai yếu tố")
@RequestMapping(AppConstant.BASE_URL + "/2fa")
@RequiredArgsConstructor
@RestController
public class TwoFAController {

  private final TwoFAService twoFAService;

  @PatchMapping("/status")
  @Operation(summary = "Kích hoạt xác thực hai yếu tố (2FA)")
  public ResponseEntity<ApiResponse<Void>> enableTwoFactorAuthentication(
      @RequestParam(value = "enable", defaultValue = "true") boolean enable) {
    twoFAService.switch2FAMethod(enable);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Thiết lập xác thực hai yếu tố (2FA) với TOTP")
  @PostMapping("/setup-totp")
  public ResponseEntity<ApiResponse<TOTPSetupResponseDTO>> enableTwoFactorAuthentication() {
    return ResponseEntity.ok(ApiResponse.success(twoFAService.setupTOTP()));
  }

  @Operation(summary = "Xác nhận xác thực hai yếu tố (2FA) với TOTP")
  @PostMapping("/confirm-setup-totp")
  public ResponseEntity<ApiResponse<TOTPVerifySetupResponseDTO>> confirmTwoFactorAuthentication(
      @RequestParam("code") String code) {
    return ResponseEntity.ok(ApiResponse.success(twoFAService.verifySetupTOTP(code)));
  }

  @Operation(summary = "Xác thực hai yếu tố (2FA) khi đăng nhập với TOTP")
  @PostMapping("/verify-totp")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> verifyTwoFactorAuthentication(
      @Valid @RequestBody TOTPLoginRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(twoFAService.verifyTOTP(request)));
  }
}
