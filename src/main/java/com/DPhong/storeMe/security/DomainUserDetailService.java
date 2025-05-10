package com.DPhong.storeMe.security;

import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusException;
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
    switch (userFromDatabase.getStatus()) {
      case UNVERIFIED:
        throw new AccountStatusException("Account is not verified") {};
      case BLOCKED:
        throw new AccountStatusException("Account is blocked") {};
      case DELETED:
        throw new AccountStatusException("Account is deleted") {};
      default:
        break;
    }
    return CustomUserDetails.builder()
        .id(userFromDatabase.getId())
        .username(userFromDatabase.getEmail())
        .password(userFromDatabase.getPasswordHash())
        .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
        .build();
  }
}
