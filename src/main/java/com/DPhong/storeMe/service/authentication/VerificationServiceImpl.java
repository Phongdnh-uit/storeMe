package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.constant.VerificationConstant;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.entity.Verification;
import com.DPhong.storeMe.enums.VerificationType;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.exception.VerificationException;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.repository.VerificationRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VerificationServiceImpl implements VerificationService {

  private final VerificationRepository verificationRepository;
  private final UserRepository userRepository;

  @Override
  public Verification createVerification(Long userId, VerificationType type) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    switch (user.getStatus()) {
      case BLOCKED:
        throw new VerificationException("User is locked");
      case DELETED:
        throw new VerificationException("User is deleted");
      default:
        break;
    }

    Verification verification = new Verification();
    verification.setUser(user);
    verification.setType(type);
    verification.setCode(UUID.randomUUID().toString());
    verification.setExpiratedAt(
        switch (type) {
          case ACTIVATION ->
              Instant.now().plusMillis(VerificationConstant.ACTIVATION_EXPIRATION_TIME);
          case FORGOT_PASSWORD ->
              Instant.now().plusMillis(VerificationConstant.FORGOT_PASSWORD_EXPIRATION_TIME);
        });
    return verificationRepository.save(verification);
  }

  @Override
  public Verification verifyCode(Long userId, String code, VerificationType type) {
    Verification verification =
        verificationRepository
            .findByUserIdAndCodeAndType(userId, code, type)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));
    if (verification.getExpiratedAt().isBefore(Instant.now())) {
      throw new VerificationException("Verification code expired");
    } else {
      verificationRepository.delete(verification);
    }
    return verification;
  }

  @Override
  public void deleteVerification(Verification verification) {
    verificationRepository.delete(verification);
  }
}
