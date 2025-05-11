package com.DPhong.storeMe.dto.user;

import com.DPhong.storeMe.enums.UserStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
  private Long id;
  private String username;
  private String email;
  private UserStatus status;
  private Instant createdAt;
  private Instant updatedAt;
  private Long createdBy;
  private Long updatedBy;
}
