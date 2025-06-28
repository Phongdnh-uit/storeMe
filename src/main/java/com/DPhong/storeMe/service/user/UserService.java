package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.enums.UserStatus;

public interface UserService {

  UserResponseDTO getCurrentAccount();

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  void updateStatus(Long userId, UserStatus status);
}
