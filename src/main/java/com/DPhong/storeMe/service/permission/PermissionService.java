package com.DPhong.storeMe.service.permission;

import com.DPhong.storeMe.dto.permission.PermissionRequestDTO;
import com.DPhong.storeMe.dto.permission.PermissionResponseDTO;
import com.DPhong.storeMe.entity.Permission;
import com.DPhong.storeMe.service.CrudService;

public interface PermissionService
    extends CrudService<Permission, Long, PermissionRequestDTO, PermissionResponseDTO> {}
