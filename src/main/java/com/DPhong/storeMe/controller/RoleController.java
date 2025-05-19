package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.role.RoleRequestDTO;
import com.DPhong.storeMe.dto.role.RoleResponseDTO;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.service.role.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Role Management", description = "Quản lý quyền truy cập")
@RequestMapping(AppConstant.BASE_URL + "/roles")
@RestController
public class RoleController extends GenericController<Role, RoleRequestDTO, RoleResponseDTO> {

  public RoleController(RoleService service) {
    super(service);
  }

  @PostMapping("{id}/assign/{userId}")
  public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
      @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
    ((RoleService) service).assignRoleToUser(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
