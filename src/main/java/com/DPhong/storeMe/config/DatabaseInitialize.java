package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.ResourceLocation;
import com.DPhong.storeMe.constant.RoleConstant;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.service.general.FileStorageService;
import lombok.RequiredArgsConstructor;
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
  private final FileStorageService fileStorageService;
  private final FolderRepository folderRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    if (userRepository.existsByUsername("admin")
        || userRepository.existsByEmail("admin@gmail.com")) {
      return;
    }
    User admin = new User();
    admin.setUsername("admin");
    admin.setEmail("admin@gmail.com");
    admin.setPasswordHash(passwordEncoder.encode("Admin123"));
    admin.setStatus(UserStatus.ACTIVE);
    admin.setLoginProvider(LoginProvider.LOCAL);
    Role adminRole =
        roleRepository
            .findByName(RoleConstant.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("Initialization error: Role not found"));
    admin.setRole(adminRole);
    admin.setCreatedBy(0L);
    admin.setUpdatedBy(0L);
    userRepository.save(admin);
    // Create a folder for the user, using the user ID as the folder name
    // The folder will be created in the USER_STORAGE_ROOT directory
    // this is the root directory for user and not save in the database
    Folder folder = new Folder();
    folder.setName(admin.getId().toString());
    folder.setSize(0L);
    folder.setUser(admin);
    String path =
        fileStorageService.createFolder(
            ResourceLocation.USER_STORAGE_ROOT, admin.getId().toString());
    folder.setPath(path);
    folderRepository.save(folder);
  }
}
