package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.enums.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

  UserResponseDTO getCurrent();

  UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

  void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

  void updateStatus(Long userId, UserStatus status);

  PageResponse<UserResponseDTO> getAll(Specification<User> spec, Pageable pageable);

  UserResponseDTO create(UserRequestDTO userRequestDTO);

  UserResponseDTO update(Long userId, UserRequestDTO userRequestDTO);

  void delete(Long userId);
}
