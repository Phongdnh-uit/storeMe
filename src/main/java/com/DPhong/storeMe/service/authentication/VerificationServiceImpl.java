package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.entity.authentication.Verification;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.VerificationType;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.exception.VerificationException;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.repository.VerificationRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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

    validateUserStatus(user);

    Verification verification = new Verification();
    verification.setUser(user);
    verification.setType(type);
    verification.setCode(UUID.randomUUID().toString());
    verification.setExpiratedAt(Instant.now().plusSeconds(type.getExpirationTime()));
    return verificationRepository.save(verification);
  }

  @Override
  public Verification verifyCode(Long userId, String code, VerificationType type) {
    Specification<Verification> specification =
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("code"), code), builder.equal(root.get("type"), type));
    // if !anonymous user, use userId to find verification
    if (userId != null) {
      specification =
          specification.and(
              (root, _, builder) -> builder.equal(root.get("user").get("id"), userId));
    }
    Verification verification =
        verificationRepository
            .findOne(specification)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));
    if (verification.getExpiratedAt().isBefore(Instant.now())) {
      verificationRepository.delete(verification);
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

  @Scheduled(cron = "0 0 0 * * ?")
  @Override
  public void cronDeleteExpiredVerifications() {
    verificationRepository.delete(
        (root, _, builder) -> builder.lessThan(root.get("expiratedAt"), Instant.now()));
  }

  // ============================ HELPER METHODS ============================
  void validateUserStatus(User user) {
    switch (user.getStatus()) {
      case BLOCKED:
        throw new AuthException(ErrorCode.USER_DISABLED);
      case DELETED:
        throw new AuthException(ErrorCode.USER_NOT_FOUND);
      default:
        break;
    }
  }
}
