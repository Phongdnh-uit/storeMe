package com.DPhong.storeMe.dto.authentication;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TOTPVerifySetupResponseDTO {
  private List<String> backupCodes;
}
