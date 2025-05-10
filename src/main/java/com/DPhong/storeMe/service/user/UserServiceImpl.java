package com.DPhong.storeMe.service.user;

import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.mapper.UserMapper;
import com.DPhong.storeMe.repository.UserRepository;
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
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
    validateUser(registerRequestDTO);
    User user = new User();
    user.setUsername(registerRequestDTO.getUsername())
        .setEmail(registerRequestDTO.getEmail())
        .setPasswordHash(passwordEncoder.encode(registerRequestDTO.getPassword()))
        .setStatus(UserStatus.UNVERIFIED);

    // TODO: send verification email
    user = userRepository.save(user);
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
}
