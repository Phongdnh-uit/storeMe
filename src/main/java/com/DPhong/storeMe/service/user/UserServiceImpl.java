package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.constant.ResourceLocation;
import com.DPhong.storeMe.constant.RoleConstant;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.UserMapper;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.general.FileStorageService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final FileStorageService fileStorageService;
  private final FolderRepository folderRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

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
            .findByName(RoleConstant.ROLE_USER)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    user.setRole(userRole);
    user = userRepository.save(user);
    // Create a folder for the user, using the user ID as the folder name
    // The folder will be created in the USER_STORAGE_ROOT directory
    // this is the root directory for user and not save in the database
    Folder folder = new Folder();
    folder.setName(user.getId().toString());
    folder.setSize(0L);
    folder.setUser(user);
    String path =
        fileStorageService.createFolder(
            ResourceLocation.USER_STORAGE_ROOT, user.getId().toString());
    folder.setPath(path);
    folderRepository.save(folder);
    return userMapper.entityToResponse(user);
  }

  /**
   * Validate user data before saving to the database.
   *
   * @param registerRequestDTO the user data to validate
   * @throws DataConflictException if the email or username already exists
   */
  private void validateUser(RegisterRequestDTO registerRequestDTO) {
    Map<String, String> errors = new HashMap<>();
    if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
      errors.put("email", "Email already exists");
    }
    if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
      errors.put("username", "Username already exists");
    }
    if (!errors.isEmpty()) {
      throw new DataConflictException("user data already exists", errors);
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
      throw new BadRequestException("Old password is incorrect");
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
        .findById(SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
