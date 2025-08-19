package com.DPhong.storeMe.constant;

import com.DPhong.storeMe.enums.RoleName;
import java.util.HashMap;
import java.util.Map;

public class View {
  public static final Map<RoleName, Class> MAPPING = new HashMap<>();

  static {
    MAPPING.put(RoleName.USER, User.class);
    MAPPING.put(RoleName.ADMIN, Admin.class);
    MAPPING.put(RoleName.SUPER_ADMIN, Admin.class);
  }

  public static class User {}

  public static class Admin {}
}
