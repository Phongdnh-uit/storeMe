package com.DPhong.storeMe.entity.authentication;

import com.DPhong.storeMe.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "backup_codes")
public class BackupCode extends BaseEntity {
  private String code;
  private Long userId;
}
