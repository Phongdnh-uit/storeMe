package com.DPhong.storeMe.dto.user;

import com.DPhong.storeMe.enums.UserStatus;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponseDTO {
  private Long id;
  private String username;
  private String email;
  private String passwordHash;
  private UserStatus status;
  private Instant createdAt;
  private Long createdBy;
  private Long updatedBy;
}
