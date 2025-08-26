package com.DPhong.storeMe.entity.authentication;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.User2FAType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_2fa_methods")
public class User2FAMethod extends BaseEntity {

  @Column(nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  private User2FAType type;

  @Column(nullable = false)
  private String secret;

  private boolean isActive;
}
