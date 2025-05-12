package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.service.GenericService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Folder", description = "Endpoint quản lý thư mục (folder) trong hệ thống")
@RestController
@RequestMapping(AppConstant.BASE_URL + "/folders")
public class FolderController
    extends GenericController<Folder, FolderRequestDTO, FolderResponseDTO> {

  public FolderController(
      GenericService<Folder, FolderRequestDTO, FolderResponseDTO> genericService) {
    super(genericService, Folder.class);
  }
}
