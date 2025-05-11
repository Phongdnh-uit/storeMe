package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.VerificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "verifications")
public class Verification extends BaseEntity {

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private Instant expiratedAt;

  @Enumerated(EnumType.STRING)
  private VerificationType type;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
