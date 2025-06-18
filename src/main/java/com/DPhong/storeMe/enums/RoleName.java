package com.DPhong.storeMe.enums;

import lombok.Getter;

@Getter
public enum RoleName {
  USER("USER", "User role with basic permissions"),
  ADMIN("ADMIN", "Administrator role with elevated permissions"),
  SUPERADMIN("SUPER_ADMIN", "System role with all permissions");

  private final String name;
  private final String description;

  RoleName(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
