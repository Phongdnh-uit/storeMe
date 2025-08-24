package com.DPhong.storeMe.security;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {

  private final Long id;

  private final String password;

  private final String email;

  private final Set<? extends GrantedAuthority> authorities;

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
}
