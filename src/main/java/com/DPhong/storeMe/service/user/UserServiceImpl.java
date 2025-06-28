package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.UserMapper;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityUtils securityUtils;

  @Override
  public UserResponseDTO getCurrent() {
    return userMapper.entityToResponse(getCurrentUser());
  }

  /**
   * Register a new user.
   *
   * @param registerRequestDTO the user data to register
   * @return the registered user data
   */
  @Override
  public UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
    validateUser(registerRequestDTO);
    User user = new User();
    user.setUsername(registerRequestDTO.getUsername())
        .setEmail(registerRequestDTO.getEmail())
        .setPasswordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
        .setStatus(UserStatus.UNVERIFIED);
    user.setLoginProvider(LoginProvider.LOCAL);

    // Set the role for the user
    Role userRole =
        roleRepository
            .findByName(RoleName.USER.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    user.setRole(userRole);
    user = userRepository.save(user);

    // Create a folder for the user: USERROOT, TRASH, SHARED
    return userMapper.entityToResponse(user);
  }

  /**
   * Validate user data before saving to the database.
   *
   * @param registerRequestDTO the user data to validate
   * @throws DataConflictException if the email or username already exists
   */
  private void validateUser(RegisterRequestDTO registerRequestDTO) {
    List<FieldError> fieldErrors = new ArrayList<>();
    if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
      fieldErrors.add(FieldError.from("email", "email đã tồn tại"));
    }
    if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
      fieldErrors.add(FieldError.from("username", "username đã tồn tại"));
    }
    if (!fieldErrors.isEmpty()) {
      throw new DataConflictException(fieldErrors);
    }
  }

  /**
   * Change the password of the current user.
   *
   * @param oldPassword the old password
   * @param newPassword the new password
   */
  @Override
  public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
    User user = getCurrentUser();
    if (!passwordEncoder.matches(
        changePasswordRequestDTO.getOldPassword(), user.getPasswordHash())) {
      List<FieldError> fieldErrors = new ArrayList<>();
      fieldErrors.add(FieldError.from("oldPassword", "Mật khẩu cũ không đúng"));
      throw new AuthException(ErrorCode.INVALID_CREDENTIALS, fieldErrors);
    }
    user.setPasswordHash(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
    userRepository.save(user);
  }

  @Override
  public void updateStatus(Long userId, UserStatus status) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    user.setStatus(status);
    userRepository.save(user);
  }

  /** Get the current user from the security context. */
  private User getCurrentUser() {
    return userRepository
        .findById(securityUtils.getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
