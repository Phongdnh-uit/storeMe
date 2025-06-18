package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.FolderConstant;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.FolderType;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.TokenProvider;
import com.DPhong.storeMe.service.authentication.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final FolderRepository folderRepository;
  private final RoleRepository roleRepository;
  private final TokenProvider tokenProvider;
  private final RefreshTokenService refreshTokenService;

  @Transactional
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
    OAuth2User user = token.getPrincipal();
    String email = user.getAttribute("email");
    String name = user.getAttribute("name");

    User userFromDatabase;
    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      userFromDatabase = new User();
      userFromDatabase.setRole(
          roleRepository
              .findByName(RoleName.USER.getName())
              .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
      userFromDatabase.setEmail(email);
      userFromDatabase.setUsername(name + "_" + Instant.now());
      userFromDatabase.setPasswordHash("");
      userFromDatabase.setLoginProvider(LoginProvider.GOOGLE);
      userFromDatabase.setStatus(UserStatus.ACTIVE);
      userFromDatabase.setTotalUsage(0L);
      userFromDatabase = userRepository.save(userFromDatabase);

      initializeFolder(userFromDatabase);

    } else {
      userFromDatabase = userOptional.get();
      if (userFromDatabase.getLoginProvider() != LoginProvider.GOOGLE) {
        throw new RuntimeException("This email is already registered with a different provider");
      }
    }
    String accessToken = tokenProvider.generateAccessToken(userFromDatabase.getId());

    String refreshToken =
        refreshTokenService.generateRefreshToken(userFromDatabase.getId()).getToken();

    AuthResponseDTO authResponseDTO =
        new AuthResponseDTO().setAccessToken(accessToken).setRefreshToken(refreshToken);

    new ObjectMapper().writeValue(response.getWriter(), authResponseDTO);
  }

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
