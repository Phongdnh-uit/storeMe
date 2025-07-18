package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sharing extends BaseEntity {

  @Column(nullable = false)
  private Long grantedBy;

  @Column(nullable = false)
  private Long grantedTo;

  @Column(nullable = false)
  private Long sharedFSNode;
}
