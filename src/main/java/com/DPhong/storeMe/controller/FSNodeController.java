package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UploadFileRequestDTO;
import com.DPhong.storeMe.entity.FSNode;
import com.DPhong.storeMe.service.fsNode.FSNodeService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File System", description = "Endpoint thao tác với hệ thống file")
@RequestMapping(AppConstant.BASE_URL + "/fs")
@RequiredArgsConstructor
@RestController
public class FSNodeController {
  private final FSNodeService fsNodeService;

  @Operation(summary = "Lấy danh sách các node trong hệ thống file")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<FSResponseDTO>>> getAllNodes(
      @RequestParam(value = "parentId", required = false) Long parentId,
      Pageable pageable,
      @Filter Specification<FSNode> specification) {
    pageable = pageable.isPaged() ? pageable : Pageable.unpaged();
    return ResponseEntity.ok(
        ApiResponse.success(fsNodeService.getAll(parentId, specification, pageable)));
  }

  @PostMapping("/folders")
  public ResponseEntity<ApiResponse<FSResponseDTO>> createFolder(
      @Valid @RequestBody CreateFolderRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(fsNodeService.createFolder(request)));
  }

  @Operation(summary = "Tải lên file vào hệ thống file")
  @PostMapping("/files/upload")
  public ResponseEntity<ApiResponse<List<FSResponseDTO>>> uploadFile(
      @Valid @RequestBody UploadFileRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(fsNodeService.uploadFiles(request)));
  }

  @PostMapping("/files/preview/{id}")
  public ResponseEntity<Resource> previewFile(@PathVariable("id") Long id) {
    Resource resource = fsNodeService.getFile(id);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(resource);
  }

  @PostMapping("/files/download/{id}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) {
    Resource resource = fsNodeService.getFile(id);
    FSResponseDTO fsResponseDTO = fsNodeService.getById(id);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + fsResponseDTO.getName() + "\"")
        .body(resource);
  }

  @Operation(summary = "Cập nhật thông tin của một node trong hệ thống file")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<FSResponseDTO>> updateNode(
      @PathVariable("id") Long id, @Valid @RequestBody UpdateFSNodeRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(fsNodeService.update(id, request)));
  }

  @Operation(summary = "Xoá một node trong hệ thống file")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteNode(@PathVariable("id") Long id) {
    fsNodeService.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Xoá nhiều node trong hệ thống file")
  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteNodes(@RequestParam("ids") List<Long> ids) {
    fsNodeService.deleteMany(ids);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
