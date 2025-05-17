package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "storage_plans")
public class StoragePlan extends BaseEntity {

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private Double price;

  /** Count of days the plan is valid. */
  private Long timeOfPlan;

  /** The maximum storage limit in bytes. */
  @Column(nullable = false)
  private Long storageLimit;

  @OneToMany(mappedBy = "storagePlan")
  private List<UserPlan> userPlans = new ArrayList<>();
}
