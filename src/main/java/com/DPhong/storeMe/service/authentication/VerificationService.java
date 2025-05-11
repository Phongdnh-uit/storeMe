package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.entity.Verification;
import com.DPhong.storeMe.enums.VerificationType;

public interface VerificationService {

  Verification createVerification(Long userId, VerificationType type);

  Verification verifyCode(Long userId, String code, VerificationType type);

  void deleteVerification(Verification verification);
}
