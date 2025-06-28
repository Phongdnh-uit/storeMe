package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RefreshTokenRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;

public interface AuthService {

  UserResponseDTO getAccount();

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  AuthResponseDTO login(LoginRequestDTO loginRequestDTO);

  AuthResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO);

  void logout(RefreshTokenRequestDTO refreshTokenRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  void verifyEmail(Long userId, String code);

  void resendVerifyEmail(Long userId);
}
