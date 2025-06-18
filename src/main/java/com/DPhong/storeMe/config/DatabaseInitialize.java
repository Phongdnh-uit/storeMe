package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.FolderConstant;
import com.DPhong.storeMe.dto.authentication.RegisterRequestDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.FolderType;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseInitialize implements ApplicationRunner {
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final FolderRepository folderRepository;
  private final Validator validator;

  @Value("${admin.username}")
  private String adminUsername;

  @Value("${admin.password}")
  private String adminPassword;

  @Value("${admin.email}")
  private String adminEmail;

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    // Initialize roles
    initializeRole();

    Role superAdminRole =
        roleRepository
            .findByName(RoleName.SUPERADMIN.getName())
            .orElseThrow(() -> new IllegalArgumentException("SUPER ADMIN role not found."));

    if (superAdminRole.getUsers().isEmpty()) {
      // Initialize super admin user if no users exist with SUPERADMIN role
      initializeSuperAdmin();
    } else {
      System.out.println("Super admin user already exists. Skipping initialization.");
    }
  }

  // ============================ ROLE ============================
  private void initializeRole() {
    for (RoleName roleName : RoleName.values()) {
      if (!roleRepository.existsByName(roleName.getName())) {
        Role role = new Role();
        role.setName(roleName.getName());
        role.setDescription(roleName.getDescription());
        roleRepository.save(role);
      }
    }
  }

  // ============================ ADMIN ============================
  private void validateAdmin() {
    RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
    registerRequestDTO.setUsername(adminUsername);
    registerRequestDTO.setPassword(adminPassword);
    registerRequestDTO.setEmail(adminEmail);

    Set<ConstraintViolation<RegisterRequestDTO>> violations =
        validator.validate(registerRequestDTO);

    if (!violations.isEmpty()) {
      System.out.println("Initializing admin user fail with the following errors:");
      for (ConstraintViolation<RegisterRequestDTO> violation : violations) {
        System.out.println(violation.getMessage());
      }
      throw new IllegalArgumentException("Invalid admin user data.");
    }

    if (userRepository.existsByUsername(adminUsername)) {
      throw new IllegalArgumentException("Admin username already exists.");
    }
    if (userRepository.existsByEmail(adminEmail)) {
      throw new IllegalArgumentException("Admin email already exists.");
    }
  }

  private void initializeSuperAdmin() {
    validateAdmin();
    User adminUser = new User();
    adminUser.setEmail(adminEmail);
    adminUser.setUsername(adminUsername);
    adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
    adminUser.setRole(
        roleRepository
            .findByName(RoleName.SUPERADMIN.getName())
            .orElseThrow(() -> new IllegalArgumentException("SUPER ADMIN role not found.")));
    adminUser.setStatus(UserStatus.ACTIVE);
    adminUser.setLoginProvider(LoginProvider.LOCAL);
    userRepository.save(adminUser);
    initializeFolder(adminUser);
  }

  // ============================ FOLDER (OPTIONAL) ============================
  private void initializeFolder(User user) {
    // Create the root folder for the user
    Folder userRootFolder = new Folder();
    userRootFolder.setUser(user);
    userRootFolder.setName(FolderConstant.USER_ROOT);
    userRootFolder.setType(FolderType.USERROOT);
    userRootFolder.setLocked(true);
    folderRepository.save(userRootFolder);

    // Create the trash folder for the user
    Folder trashFolder = new Folder();
    trashFolder.setUser(user);
    trashFolder.setName(FolderConstant.TRASH);
    trashFolder.setType(FolderType.TRASH);
    trashFolder.setLocked(true);
    folderRepository.save(trashFolder);

    // Create the shared folder for the user
    Folder sharedFolder = new Folder();
    sharedFolder.setUser(user);
    sharedFolder.setName(FolderConstant.SHARED);
    sharedFolder.setType(FolderType.SHARED);
    sharedFolder.setLocked(true);
    folderRepository.save(sharedFolder);
  }
}
