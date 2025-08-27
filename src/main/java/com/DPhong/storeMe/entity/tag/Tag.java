package com.DPhong.storeMe.entity.tag;

import com.DPhong.storeMe.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {
  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private Long ownerId;
}
