package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.service.CrudService;

public interface UserService extends CrudService<User, Long, UserRequestDTO, UserResponseDTO> {

  UserResponseDTO getCurrent();

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  void updateStatus(Long userId, UserStatus status);
}
