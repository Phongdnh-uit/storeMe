package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoint xác thực người dùng")
@RequestMapping(AppConstant.BASE_URL + "/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
  private final UserService userService;

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

  @PostMapping("/login")
  public String login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    return "login";
  }

  @PostMapping("/verify-email")
  public String verifyEmail() {
    return "verify-email";
  }

  @PostMapping("/reset-password")
  public String resetPassword() {
    return "reset-password";
  }

  @PostMapping("/forgot-password")
  public String forgotPassword() {
    return "forgot-password";
  }

  @PostMapping("/logout")
  public String logout() {
    return "logout";
  }
}
