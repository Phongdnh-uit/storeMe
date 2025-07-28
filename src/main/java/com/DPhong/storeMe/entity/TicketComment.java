package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_comments")
public class TicketComment extends BaseEntity {
  @Column(nullable = false, length = 500)
  private String content;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long ticketId;
}
