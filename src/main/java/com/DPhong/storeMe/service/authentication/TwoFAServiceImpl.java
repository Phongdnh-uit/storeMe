package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.constant.RedisKey;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPLoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.TOTPSetupResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPVerifySetupResponseDTO;
import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.entity.authentication.User2FAMethod;
import com.DPhong.storeMe.entity.authentication.Verification;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.User2FAType;
import com.DPhong.storeMe.enums.VerificationType;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.repository.User2FAMethodRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.security.TokenProvider;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TwoFAServiceImpl implements TwoFAService {

  private final GAService gaService;
  private final SecurityUtils securityUtils;
  private final UserRepository userRepository;
  private final User2FAMethodRepository user2FAMethodRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final VerificationService verificationService;
  private final RefreshTokenService refreshTokenService;
  private final TokenProvider tokenProvider;

  // ============================ SWITCH 2FA METHOD ============================
  @Override
  public void switch2FAMethod(boolean enable) {
    Long userId = securityUtils.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    if (enable) {
      // Check if user already has a TOTP method
      boolean hasTwoFAMethod =
          user2FAMethodRepository.exists(
              (root, _, builder) -> builder.and(builder.equal(root.get("userId"), userId)));
      if (!hasTwoFAMethod) {
        throw new AuthException(ErrorCode.DATA_INTEGRITY_VIOLATION, "No two-factor method found");
      }
      user.set2FAEnabled(true);
    } else {
      user.set2FAEnabled(false);
    }
    userRepository.save(user);
  }

  // ============================ SETUP TOTP ============================
  @Override
  public TOTPSetupResponseDTO setupTOTP() {
    Long userId = securityUtils.getCurrentUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    String secretKey = gaService.generateKey();
    redisTemplate.opsForValue().set(RedisKey.TOTP_SETUP + userId, secretKey, 5, TimeUnit.MINUTES);
    String qrCodeUrl = gaService.generateQRUrl(secretKey, user.getUsername());
    TOTPSetupResponseDTO totpResponseDTO = new TOTPSetupResponseDTO();
    totpResponseDTO.setSecret(secretKey);
    totpResponseDTO.setQrCodeUrl(qrCodeUrl);
    return totpResponseDTO;
  }

  // ============================ VERIFY TOTP SETUP ============================
  @Override
  public TOTPVerifySetupResponseDTO verifySetupTOTP(String code) {
    Long userId = securityUtils.getCurrentUserId();
    String secretKey = (String) redisTemplate.opsForValue().get(RedisKey.TOTP_SETUP + userId);
    if (secretKey == null) {
      throw new ResourceNotFoundException("TOTP setup not found or expired");
    }
    Integer codeInt = Integer.parseInt(code);
    boolean isValid = gaService.isValid(secretKey, codeInt);
    if (!isValid) {
      throw new AuthException(ErrorCode.AUTH_FAILED, "Invalid TOTP code");
    }
    // Save TOTP method for user
    // delete old
    user2FAMethodRepository.delete(
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("userId"), userId),
                builder.equal(root.get("type"), User2FAType.TOTP)));
    // save new
    User2FAMethod user2FAMethod = new User2FAMethod();
    user2FAMethod.setUserId(userId);
    user2FAMethod.setType(User2FAType.TOTP);
    user2FAMethod.setSecret(secretKey);
    user2FAMethodRepository.save(user2FAMethod);
    redisTemplate.delete(RedisKey.TOTP_SETUP + userId);
    TOTPVerifySetupResponseDTO responseDTO = new TOTPVerifySetupResponseDTO();
    // TODO: generate backup_code
    return responseDTO;
  }

  // ============================ VERIFY TOTP ============================
  @Override
  public AuthResponseDTO verifyTOTP(TOTPLoginRequestDTO request) {
    // 1. ---- Verify pending code ----
    // userId is null for anonymous user
    Verification verification =
        verificationService.verifyCode(
            null, request.getPendingCode(), VerificationType.TWO_FACTOR_AUTHENTICATION);
    Long userId = verification.getUser().getId();
    User2FAMethod user2FAMethod =
        user2FAMethodRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("userId"), userId),
                        builder.equal(root.get("type"), User2FAType.TOTP)))
            .orElseThrow(() -> new ResourceNotFoundException("TOTP method not found for user"));
    Integer codeInt = Integer.parseInt(request.getCode());
    boolean isValid = gaService.isValid(user2FAMethod.getSecret(), codeInt);
    if (!isValid) {
      throw new AuthException(ErrorCode.AUTH_FAILED, "Invalid TOTP code");
    }
    String accessToken = tokenProvider.generateAccessToken(userId);
    String refreshToken = refreshTokenService.generateRefreshToken(userId).getToken();
    AuthResponseDTO authResponseDTO = new AuthResponseDTO();
    authResponseDTO.setAccessToken(accessToken);
    authResponseDTO.setRefreshToken(refreshToken);
    return authResponseDTO;
  }
}
