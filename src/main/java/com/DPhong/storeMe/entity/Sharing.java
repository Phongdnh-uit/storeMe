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

  @Column(name = "shared_fs_node", nullable = false)
  private Long sharedFSNode;
}
