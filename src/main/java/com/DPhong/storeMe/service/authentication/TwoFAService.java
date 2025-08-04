package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPLoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.TOTPSetupResponseDTO;
import com.DPhong.storeMe.dto.authentication.TOTPVerifySetupResponseDTO;

public interface TwoFAService {

  void switch2FAMethod(boolean enable);

  TOTPSetupResponseDTO setupTOTP();

  TOTPVerifySetupResponseDTO verifySetupTOTP(String code);

  AuthResponseDTO verifyTOTP(TOTPLoginRequestDTO request);
}
