package com.DPhong.storeMe.dto.sharing;

import com.DPhong.storeMe.enums.SharingType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSharingRequestDTO {
  private List<SharingType> permissions;
}
