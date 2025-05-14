package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "storage_plans")
public class StoragePlan extends BaseEntity {

  @Column(name = "plan_name", nullable = false)
  private String name;

  private String description;

  /** The maximum storage limit in bytes. */
  @Column(nullable = false)
  private Long storageLimit;
}
