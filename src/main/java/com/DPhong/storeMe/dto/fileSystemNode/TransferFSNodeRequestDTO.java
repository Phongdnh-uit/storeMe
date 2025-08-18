package com.DPhong.storeMe.dto.fileSystemNode;

import com.DPhong.storeMe.enums.FSAction;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferFSNodeRequestDTO {
  private Long destinationId;

  @NotNull private FSAction action;
}
