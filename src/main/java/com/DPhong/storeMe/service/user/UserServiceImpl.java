package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.dto.authentication.ChangePasswordRequestDTO;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.entity.authorization.Role;
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
import com.DPhong.storeMe.service.GenericService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericService<User, UserRequestDTO, UserResponseDTO>
    implements UserService {

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      UserRepository repository,
      UserMapper mapper,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder) {
    super(repository, mapper);
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserResponseDTO getCurrent() {
    return mapper.entityToResponse(getCurrentUser());
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
    user = repository.save(user);

    // Create a folder for the user: USERROOT, TRASH, SHARED
    return mapper.entityToResponse(user);
  }

  /**
   * Validate user data before saving to the database.
   *
   * @param registerRequestDTO the user data to validate
   * @throws DataConflictException if the email or username already exists
   */
  private void validateUser(RegisterRequestDTO registerRequestDTO) {
    List<FieldError> fieldErrors = new ArrayList<>();
    if (((UserRepository) repository).existsByEmail(registerRequestDTO.getEmail())) {
      fieldErrors.add(FieldError.from("email", "email đã tồn tại"));
    }
    if (((UserRepository) repository).existsByUsername(registerRequestDTO.getUsername())) {
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
    repository.save(user);
  }

  @Override
  public void updateStatus(Long userId, UserStatus status) {
    User user =
        repository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    user.setStatus(status);
    repository.save(user);
  }

  /** Get the current user from the security context. */
  private User getCurrentUser() {
    return repository
        .findById(SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  // ============================ CREATE ============================
  @Override
  protected void beforeCreateMapper(UserRequestDTO request) {
    validateUser(0L, request);
  }

  @Override
  protected void afterCreateMapper(UserRequestDTO request, User entity) {
    entity.setStatus(UserStatus.UNVERIFIED);
    entity.setLoginProvider(LoginProvider.LOCAL);
    entity.setRole(
        roleRepository
            .findById(request.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
  }

  // ============================ UPDATE ============================

  @Override
  protected void beforeUpdateMapper(Long id, UserRequestDTO request, User oldEntity) {
    validateUser(id, request);
  }

  @Override
  protected void afterUpdateMapper(Long id, UserRequestDTO request, User newEntity) {
    newEntity.setRole(
        roleRepository
            .findById(request.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
  }

  /**
   * @param userId the ID of the user to validate ( 0 <= for new users)
   * @param userRequestDTO the user data to validate
   */
  void validateUser(Long userId, UserRequestDTO userRequestDTO) {
    List<FieldError> fieldErrors = new ArrayList<>();
    if (repository.exists(
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("email"), userRequestDTO.getEmail()),
                builder.notEqual(root.get("id"), userId)))) {
      fieldErrors.add(FieldError.from("email", "Email đã tồn tại"));
    }
    if (!fieldErrors.isEmpty()) {
      throw new DataConflictException(fieldErrors);
    }
  }
}
