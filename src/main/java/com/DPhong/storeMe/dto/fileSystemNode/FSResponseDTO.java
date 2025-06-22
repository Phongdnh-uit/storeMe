package com.DPhong.storeMe.dto.fileSystemNode;

import com.DPhong.storeMe.entity.BaseEntity;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FSResponseDTO extends BaseEntity {

  private String name;

  private Long userId;

  private Long size;

  private List<Long> ancestor;

  private Instant deletedAt;
}
