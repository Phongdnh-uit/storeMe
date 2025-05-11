package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private Instant expiratedAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
