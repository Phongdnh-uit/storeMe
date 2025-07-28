package com.DPhong.storeMe.dto.support;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.PriorityLevel;
import com.DPhong.storeMe.enums.TicketStatus;
import com.DPhong.storeMe.enums.TicketType;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponseDTO extends BaseEntity {
  private String subject;

  private String description;

  private TicketType ticketType;

  private PriorityLevel priority;

  private TicketStatus status;

  private Long userId;

  private Instant closedAt;
}
