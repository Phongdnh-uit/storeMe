package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.permission.PermissionRequestDTO;
import com.DPhong.storeMe.dto.permission.PermissionResponseDTO;
import com.DPhong.storeMe.entity.authorization.Permission;
import com.DPhong.storeMe.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Permission", description = "Quản lý quyền hạn của vai trò")
@RequestMapping(AppConstant.BASE_URL + "/permissions")
@RestController
public class PermissionController
    extends GenericController<Permission, PermissionRequestDTO, PermissionResponseDTO> {

  public PermissionController(PermissionService service) {
    super(service);
  }
}
