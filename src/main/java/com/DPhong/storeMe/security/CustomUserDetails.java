package com.DPhong.storeMe.security;

import java.util.Set;
import lombok.Builder;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Value
public class CustomUserDetails implements UserDetails {

  private Long id;

  private String password;

  private final String username;

  private final Set<GrantedAuthority> authorities;
}
