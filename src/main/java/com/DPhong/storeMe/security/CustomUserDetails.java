package com.DPhong.storeMe.security;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Builder
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

  private final Long id;

  private final String password;

  private final String email;

  private final Set<? extends GrantedAuthority> authorities;

  private final Map<String, Object> attributes;

  @Override
  public String getUsername() {
    return email;
  }

  private boolean is2faEnabled;

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return String.valueOf(id);
  }
}
