package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.file.FileRequestDTO;
import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File", description = "Endpoint quản lý file")
@RequiredArgsConstructor
@RequestMapping(AppConstant.BASE_URL + "/file")
@RestController
public class FileController {

  private final FileService fileService;

  @Operation(summary = "Lấy thông tin file")
  @GetMapping("/info/{id}")
  public ResponseEntity<ApiResponse<FileResponseDTO>> getFileInfo(@PathVariable("id") Long id) {
    FileResponseDTO fileResponseDTO = fileService.getFileInfo(id);
    return ResponseEntity.ok(ApiResponse.success(fileResponseDTO));
  }

  @Operation(summary = "Tải lên file")
  @PostMapping("/upload")
  public ResponseEntity<ApiResponse<List<FileResponseDTO>>> uploadFile(
      @Valid @ModelAttribute FileRequestDTO fileRequestDTO) {
    List<FileResponseDTO> fileResponseDTOs = fileService.uploadFile(fileRequestDTO);
    return ResponseEntity.ok(ApiResponse.success(fileResponseDTOs));
  }

  @Operation(summary = "Xem trước file")
  @PostMapping(
      name = "/preview/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> previewFile(@PathVariable("id") Long id) {
    Resource resource = fileService.serveFile(id);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  @Operation(summary = "Download file")
  @PostMapping(
      name = "/download/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) {
    Resource resource = fileService.serveFile(id);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  @Operation(summary = "Xóa file")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable("id") Long id) {
    fileService.deleteFile(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Xóa nhiều file")
  @DeleteMapping("/delete")
  public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestBody Set<Long> fileIds) {
    fileService.deleteManyFiles(fileIds);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
