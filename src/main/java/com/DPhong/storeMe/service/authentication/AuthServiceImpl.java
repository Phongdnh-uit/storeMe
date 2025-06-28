package com.DPhong.storeMe.service.authentication;

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
import com.DPhong.storeMe.service.general.MailService;
import com.DPhong.storeMe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final RefreshTokenService refreshTokenService;
  private final TokenProvider tokenProvider;
  private final MailService mailService;
  private final VerificationService verificationService;
  private final UserService userService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtils securityUtils;

  // ============================ REGISTER USER ============================
  @Override
  public UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
    // 1. ---- Register user ----
    UserResponseDTO userResponseDTO = userService.registerUser(registerRequestDTO);
    // 2. ---- Send activation email ----
    Verification verification =
        verificationService.createVerification(
            userResponseDTO.getId(), VerificationType.ACTIVATION);
    mailService.sendActivationEmail(
        userResponseDTO.getEmail(), userResponseDTO.getId(), verification.getCode());
    return userResponseDTO;
  }

  // ============================ LOGIN ============================
  @Override
  public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
    // 1. ---- Authenticate user ----
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 2. ---- Set authentication in SecurityContext ----
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 3. ---- Generate access token and refresh token ----
    Long userId = securityUtils.getCurrentUserId();

    String accessToken = tokenProvider.generateAccessToken(userId);

    String refreshToken = refreshTokenService.generateRefreshToken(userId).getToken();

    AuthResponseDTO authResponseDTO = new AuthResponseDTO();
    authResponseDTO.setAccessToken(accessToken);
    authResponseDTO.setRefreshToken(refreshToken);

    return authResponseDTO;
  }

  // ============================ REFRESH ACCESS TOKEN ============================
  @Override
  public AuthResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
    // 1. ---- Verify refresh token ----
    RefreshToken refreshToken =
        refreshTokenService.verifyRefreshToken(refreshTokenRequestDTO.getRefreshToken());
    // 2. ---- Delete old refresh token ----
    refreshTokenService.deleteRefreshToken(refreshToken);

    // 3. ---- Generate new access token and refresh token ----
    Long userId = refreshToken.getUser().getId();
    String accessToken = tokenProvider.generateAccessToken(userId);
    String newRefreshToken = refreshTokenService.generateRefreshToken(userId).getToken();

    AuthResponseDTO authResponseDTO = new AuthResponseDTO();
    authResponseDTO.setAccessToken(accessToken);
    authResponseDTO.setRefreshToken(newRefreshToken);

    return authResponseDTO;
  }

  // ============================ LOGOUT ============================
  @Override
  public void logout(RefreshTokenRequestDTO refreshTokenRequestDTO) {
    // 1. ---- Verify refresh token ----
    RefreshToken refreshToken =
        refreshTokenService.verifyRefreshToken(refreshTokenRequestDTO.getRefreshToken());
    // 2. ---- Delete old refresh token ----
    refreshTokenService.deleteRefreshToken(refreshToken);
  }

  // ============================ GET ACCOUNT ============================
  @Override
  public UserResponseDTO getAccount() {
    return userService.getCurrent();
  }

  // ============================ CHANGE PASSWORD ============================
  @Override
  public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
    userService.changePassword(changePasswordRequestDTO);
  }

  // ============================ VERIFY EMAIL ============================
  @Override
  public void verifyEmail(Long userId, String code) {
    Verification verification =
        verificationService.verifyCode(userId, code, VerificationType.ACTIVATION);
    verificationService.deleteVerification(verification);
    userService.updateStatus(userId, UserStatus.ACTIVE);
  }

  // ============================ RESEND VERIFY EMAIL ============================
  @Override
  public void resendVerifyEmail(Long userId) {
    // 1. ---- Create new verification ----
    Verification verification =
        verificationService.createVerification(userId, VerificationType.ACTIVATION);
    // 2. ---- Delete old if exists ----
    verification.getUser().getVerifications().stream()
        .filter(
            v -> v.getType() == VerificationType.ACTIVATION && v.getId() != verification.getId())
        .forEach(verificationService::deleteVerification);
    mailService.sendActivationEmail(
        verification.getUser().getEmail(), userId, verification.getCode());
  }
}
