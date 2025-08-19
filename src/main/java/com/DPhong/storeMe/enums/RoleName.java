package com.DPhong.storeMe.enums;

import lombok.Getter;

@Getter
public enum RoleName {
  USER("USER", "User role with basic permissions"),
  ADMIN("ADMIN", "Administrator role with elevated permissions"),
  SUPER_ADMIN("SUPER_ADMIN", "System role with all permissions");

  private final String name;
  private final String description;

  RoleName(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static RoleName fromString(String name) {
    if (name.startsWith("ROLE_")) {
      name = name.substring(5);
    }
    for (RoleName role : RoleName.values()) {
      if (role.name.equalsIgnoreCase(name)) {
        return role;
      }
    }
    throw new IllegalArgumentException(
        "No enum constant " + RoleName.class.getCanonicalName() + "." + name);
  }
}
