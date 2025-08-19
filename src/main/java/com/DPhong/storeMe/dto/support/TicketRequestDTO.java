package com.DPhong.storeMe.dto.support;

import com.DPhong.storeMe.enums.TicketType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequestDTO {
  private String subject;

  private String description;

  private TicketType ticketType;
}
