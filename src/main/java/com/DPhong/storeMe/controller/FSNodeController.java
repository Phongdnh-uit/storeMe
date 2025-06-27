package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.service.fsNode.FSNodeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File System", description = "Endpoint thao tác với hệ thống file")
@RequestMapping(AppConstant.BASE_URL + "/fs")
@RequiredArgsConstructor
@RestController
public class FSNodeController {
  private final FSNodeService fsNodeService;

  @PostMapping("/create/folder")
  public ResponseEntity<ApiResponse> createFolder(@Valid @RequestBody CreateFolderRequestDTO request) {

  }
}
