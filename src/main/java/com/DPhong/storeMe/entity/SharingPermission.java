package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.SharingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sharing_permissions")
public class SharingPermission extends BaseEntity {
  @Column(nullable = false)
  private Long sharingId;

  private SharingType sharingType;
}
