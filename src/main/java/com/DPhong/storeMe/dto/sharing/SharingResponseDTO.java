package com.DPhong.storeMe.dto.sharing;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.SharingType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharingResponseDTO extends BaseEntity {
  private Long grantedBy;

  private Long grantedTo;

  private Long sharedFSNode;

  List<SharingType> permissions;
}
