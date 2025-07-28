package com.DPhong.storeMe.dto.support;

import com.DPhong.storeMe.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCommentResponseDTO extends BaseEntity {
  private String content;
  private Long userId;
  private Long ticketId;
}
