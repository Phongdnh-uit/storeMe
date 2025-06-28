package com.DPhong.storeMe.security;

import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.LoginProvider;
import com.DPhong.storeMe.exception.AuthException;
import com.DPhong.storeMe.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DomainUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String login = username.toLowerCase();
    User userFromDatabase =
        userRepository
            .findByEmail(login)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User " + login + " was not found in the database"));
    if (userFromDatabase.getLoginProvider() != LoginProvider.LOCAL) {
      throw new AuthException(ErrorCode.LOGIN_PROVIDER_NOT_SUPPORTED);
    }
    switch (userFromDatabase.getStatus()) {
      case UNVERIFIED:
        throw new AuthException(ErrorCode.USER_UNVERIFIED);
      case BLOCKED:
        throw new AuthException(ErrorCode.USER_DISABLED);
      case DELETED:
        throw new AuthException(ErrorCode.USER_NOT_FOUND);
      default:
        break;
    }
    return CustomUserDetails.builder()
        .id(userFromDatabase.getId())
        .username(userFromDatabase.getEmail())
        .password(userFromDatabase.getPasswordHash())
        .authorities(
            Set.of(new SimpleGrantedAuthority("ROLE_" + userFromDatabase.getRole().getName())))
        .build();
  }
}
