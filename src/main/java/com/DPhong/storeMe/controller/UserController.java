package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.authentication.User;
import com.DPhong.storeMe.enums.UserStatus;
import com.DPhong.storeMe.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Quản lý người dùng")
@RequestMapping("/users")
@RestController
public class UserController extends GenericController<User, UserRequestDTO, UserResponseDTO> {
  public UserController(UserService service) {
    super(service);
  }

  @Operation(summary = "Cập nhật trạng thái người dùng")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> updateUserStatus(
      @PathVariable("id") Long id, @RequestParam("status") UserStatus userStatus) {
    ((UserService) service).updateStatus(id, userStatus);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
