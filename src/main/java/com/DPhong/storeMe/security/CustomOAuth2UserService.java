package com.DPhong.storeMe.security;

import com.DPhong.storeMe.entity.AuthAccount;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.repository.AuthAccountRepository;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.oauth2.OAuth2UserInfo;
import com.DPhong.storeMe.security.oauth2.OAuth2UserInfoFactory;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;
  private final AuthAccountRepository authAccountRepository;
  private final RoleRepository roleRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String provider = userRequest.getClientRegistration().getRegistrationId();
    String providerUserId = oAuth2User.getName();
    String email = (String) oAuth2User.getAttribute("email");

    Optional<AuthAccount> authAccount =
        authAccountRepository.findOne(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("provider"), provider),
                    builder.equal(root.get("providerUserId"), providerUserId)));
    User user = null;
    if (authAccount.isPresent()) {
      user =
          userRepository
              .findById(authAccount.get().getUseId())
              .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    } else {
      OAuth2UserInfo oAuth2UserInfo =
          OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
      if (email == null) {
        throw new IllegalArgumentException("Email not found from OAuth2 provider");
      }
      user = userRepository.findByEmail(email).orElse(null);
      if (user == null) {
        user = new User();
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setUsername(oAuth2UserInfo.getName());
        user.setRole(
            roleRepository
                .findByName("" + RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found")));
        if (provider.equals("google")) {
          user.setStatus(UserStatus.ACTIVE);
        } else {
          user.setStatus(UserStatus.UNVERIFIED);
        }
        user = userRepository.save(user);
      }
      AuthAccount newAuthAccount = new AuthAccount();
      newAuthAccount.setProvider(oAuth2UserInfo.getProvider());
      newAuthAccount.setProviderUserId(providerUserId);
      newAuthAccount.setUseId(user.getId());
      authAccountRepository.save(newAuthAccount);
    }
    return CustomUserDetails.builder()
        .id(user.getId())
        .email(user.getEmail())
        .authorities(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())))
        .attributes(oAuth2User.getAttributes())
        .is2faEnabled(user.is2FAEnabled())
        .build();
  }
}
