package com.DPhong.storeMe.dto.authentication;

import com.DPhong.storeMe.enums.User2FAType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFAChallengeResponseDTO {
  private List<User2FAType> methods;
  private String pendingCode;
}
