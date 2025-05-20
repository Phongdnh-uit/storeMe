package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.ResourceLocation;
import com.DPhong.storeMe.constant.RoleConstant;
import com.DPhong.storeMe.dto.authentication.AuthResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.TokenProvider;
import com.DPhong.storeMe.service.authentication.RefreshTokenService;
import com.DPhong.storeMe.service.general.FileStorageService;
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
  private final FileStorageService fileStorageService;
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
              .findByName(RoleConstant.ROLE_USER)
              .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
      userFromDatabase.setEmail(email);
      userFromDatabase.setUsername(name + "_" + Instant.now());
      userFromDatabase.setPasswordHash("");
      userFromDatabase.setLoginProvider(LoginProvider.GOOGLE);
      userFromDatabase.setStatus(UserStatus.ACTIVE);
      userFromDatabase.setTotalUsage(0L);
      userFromDatabase = userRepository.save(userFromDatabase);
      Folder folder = new Folder();
      folder.setName(userFromDatabase.getId().toString());
      folder.setSize(0L);
      folder.setUser(userFromDatabase);
      String path =
          fileStorageService.createFolder(
              ResourceLocation.USER_STORAGE_ROOT, userFromDatabase.getId().toString());
      folder.setPath(path);
      folderRepository.save(folder);
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
}
