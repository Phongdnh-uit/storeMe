package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.entity.FSNode;

public interface FSPermissionService {
  void checkCanRead(Long userId, FSNode fsNodeId);

  void checkCanWrite(Long userId, FSNode fsNodeId);

  void checkCanDelete(Long userId, FSNode fsNodeId);
}
