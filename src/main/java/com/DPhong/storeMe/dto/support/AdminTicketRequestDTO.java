package com.DPhong.storeMe.dto.support;

import com.DPhong.storeMe.enums.PriorityLevel;
import com.DPhong.storeMe.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminTicketRequestDTO extends TicketRequestDTO {
  @NotNull private PriorityLevel priority;

  @NotNull private TicketStatus status;

  @NotNull private Long userId;
}
