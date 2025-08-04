package com.DPhong.storeMe.enums;

import com.DPhong.storeMe.constant.VerificationConstant;
import lombok.Getter;

@Getter
public enum VerificationType {
  ACTIVATION(VerificationConstant.ACTIVATION_EXPIRATION_TIME),
  FORGOT_PASSWORD(VerificationConstant.FORGOT_PASSWORD_EXPIRATION_TIME),
  TWO_FACTOR_AUTHENTICATION(VerificationConstant.TWO_FACTOR_AUTHENTICATION_EXPIRATION_TIME);

  private Long expirationTime;

  VerificationType(Long expirationTime) {
    this.expirationTime = expirationTime;
  }
}
