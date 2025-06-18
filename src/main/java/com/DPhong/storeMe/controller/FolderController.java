package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.service.GenericService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Folder", description = "Endpoint quản lý thư mục (folder) trong hệ thống")
@RestController
@RequestMapping(AppConstant.BASE_URL + "/folders")
public class FolderController
    extends GenericController<Folder, FolderRequestDTO, FolderResponseDTO> {

  public FolderController(
      GenericService<Folder, FolderRequestDTO, FolderResponseDTO> genericService) {
    super(genericService);
  }

  @Operation(summary = "Lấy danh sách thư mục tính theo thư mục gốc của người dùng")
  @Override
  public ResponseEntity<ApiResponse<PageResponse<FolderResponseDTO>>> getAll(
      @ParameterObject Pageable pageable, @Filter Specification<Folder> specification) {
    return super.getAll(pageable, specification);
  }
}
