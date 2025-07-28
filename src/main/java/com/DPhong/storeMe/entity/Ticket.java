package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.PriorityLevel;
import com.DPhong.storeMe.enums.TicketStatus;
import com.DPhong.storeMe.enums.TicketType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "support_tickets")
public class Ticket extends BaseEntity {
  @Column(nullable = false, length = 255)
  private String subject;

  private String description;

  @Column(nullable = false)
  private TicketType ticketType;

  @Column(nullable = false)
  private PriorityLevel priority;

  @Column(nullable = false)
  private TicketStatus status;

  @Column(nullable = false)
  private Long userId;

  private Instant closedAt;
}
