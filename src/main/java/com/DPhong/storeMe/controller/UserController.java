package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.service.user.UserService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(AppConstant.BASE_URL + "/users")
@RequiredArgsConstructor
@RestController
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> getAllUsers(
      @Filter Specification<User> spec, @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(userService.getAll(spec, pageable)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
      @Valid @RequestBody UserRequestDTO userRequestDTO) {
    return ResponseEntity.ok(ApiResponse.success(userService.create(userRequestDTO)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
      @PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
    return ResponseEntity.ok(ApiResponse.success(userService.update(id, userRequestDTO)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {
    userService.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
