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
@Table(name = "user_plans")
public class UserPlan extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "storage_plan_id", nullable = false)
  private StoragePlan storagePlan;

  @Column(nullable = false)
  private boolean isActive;

  @Column(nullable = false)
  private Instant assignedAt;

  @Column(nullable = false)
  private Instant expiredAt;
}
