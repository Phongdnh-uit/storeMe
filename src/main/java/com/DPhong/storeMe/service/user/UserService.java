package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;

public interface UserService {

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);
}
