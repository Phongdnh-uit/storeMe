package com.DPhong.storeMe.entity.authentication;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.LoginProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "auth_accounts")
public class AuthAccount extends BaseEntity {
  @Column(nullable = false)
  private Long useId;

  @Column(nullable = false)
  private LoginProvider provider;

  @Column(nullable = false)
  private String providerUserId;

  private Instant linkedAt = Instant.now();
}
