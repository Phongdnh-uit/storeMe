package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.LoginRequestDTO;
import com.DPhong.storeMe.dto.authentication.RefreshTokenRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.authentication.ResetPasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.SendVerifyEmailRequestDTO;
import com.DPhong.storeMe.dto.authentication.UpdateAccountRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;

public interface AuthService {

  UserResponseDTO getAccount();

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  Object login(LoginRequestDTO loginRequestDTO);

  AuthResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO);

  void logout(RefreshTokenRequestDTO refreshTokenRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  void verifyEmail(Long userId, String code);

  void resendVerifyEmail(SendVerifyEmailRequestDTO request);

  void sendForgotPasswordEmail(String email);

  void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);

  UserResponseDTO updateAccount(UpdateAccountRequestDTO updateAccountRequestDTO);
}
