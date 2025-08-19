package com.DPhong.storeMe.dto.user;

import com.DPhong.storeMe.constant.View;
import com.DPhong.storeMe.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
  private Long id;
  private String username;
  private String email;
  private Long totalUsage;
  private UserStatus status;
  private boolean is2FAEnabled;
  private Instant createdAt;
  private Instant updatedAt;
  private Long createdBy;
  private Long updatedBy;

  @JsonView(View.Admin.class)
  private String roleName;
}
