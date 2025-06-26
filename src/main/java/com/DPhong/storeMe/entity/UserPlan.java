package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "user_plans",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "is_active"})})
public class UserPlan extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "storage_plan_id", nullable = false)
  private StoragePlan storagePlan;

  @Column(nullable = false)
  private boolean isActive = true;

  @Column(nullable = false)
  private Instant expiredAt;
}
