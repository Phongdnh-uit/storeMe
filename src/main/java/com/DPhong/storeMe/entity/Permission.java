package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.HttpMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {
  private String description;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String urlPattern;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private HttpMethod method;
}
