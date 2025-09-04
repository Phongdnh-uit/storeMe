package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.sharing.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.sharing.SharingResponseDTO;
import com.DPhong.storeMe.dto.sharing.UpdateSharingRequestDTO;
import com.DPhong.storeMe.entity.sharing.Sharing;
import com.DPhong.storeMe.service.fsNode.SharingService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sharing", description = "Chia sẻ dữ liệu")
@RequiredArgsConstructor
@RestController
public class SharingController {
  private final SharingService sharingService;

  @Operation(
      summary = "Lấy danh sách chia sẻ",
      description = "Lấy danh sách chia sẻ với phân trang và lọc")
  @GetMapping("/sharing/shared-by-me")
  public ResponseEntity<ApiResponse<PageResponse<SharingResponseDTO>>> getSharedByMe(
      @ParameterObject Pageable pageable, @Filter Specification<Sharing> specification) {
    pageable = pageable.isPaged() ? pageable : Pageable.unpaged();
    return ResponseEntity.ok(
        ApiResponse.success(sharingService.getAllSharedByMe(specification, pageable)));
  }

  @Operation(
      summary = "Lấy danh sách chia sẻ với tôi",
      description = "Lấy danh sách chia sẻ với tôi với phân trang và lọc")
  @GetMapping("/sharing/shared-with-me")
  public ResponseEntity<ApiResponse<PageResponse<SharingResponseDTO>>> getSharedWithMe(
      @ParameterObject Pageable pageable, @Filter Specification<Sharing> specification) {
    pageable = pageable.isPaged() ? pageable : Pageable.unpaged();
    return ResponseEntity.ok(
        ApiResponse.success(sharingService.getAllSharedWithMe(specification, pageable)));
  }

  @Operation(summary = "Tạo chia sẻ", description = "Tạo chia sẻ cho một nút hệ thống tập tin")
  @PostMapping("/fs-nodes/{fsNodeId}/sharing")
  public ResponseEntity<ApiResponse<SharingResponseDTO>> createSharing(
      @PathVariable("fsNodeId") Long fsNodeId,
      @RequestBody CreateSharingRequestDTO createSharingRequestDTO) {
    return ResponseEntity.ok(
        ApiResponse.success(sharingService.create(fsNodeId, createSharingRequestDTO)));
  }

  @Operation(summary = "Cập nhật chia sẻ", description = "Cập nhật thông tin chia sẻ")
  @PutMapping("/sharing/{id}")
  public void updateSharing(
      @PathVariable("id") Long id, @RequestBody UpdateSharingRequestDTO updateSharingRequestDTO) {
    sharingService.update(id, updateSharingRequestDTO);
  }

  @Operation(summary = "Xoá chia sẻ", description = "Xoá một chia sẻ theo ID")
  @DeleteMapping("/sharing/{id}")
  public void deleteSharing(@PathVariable("id") Long id) {
    sharingService.delete(id);
  }
}
