package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.fileSystemNode.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateSharingRequestDTO;
import com.DPhong.storeMe.service.fsNode.SharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SharingController {
  private final SharingService sharingService;

  @PostMapping("/fs-nodes/{fsNodeId}/sharing")
  public void createSharing(
      @PathVariable Long fsNodeId, @RequestBody CreateSharingRequestDTO createSharingRequestDTO) {
    sharingService.create(fsNodeId, createSharingRequestDTO);
  }

  @PutMapping("/sharing/{id}")
  public void updateSharing(
      @PathVariable Long id, @RequestBody UpdateSharingRequestDTO updateSharingRequestDTO) {
    sharingService.update(id, updateSharingRequestDTO);
  }

  @DeleteMapping("/sharing/{id}")
  public void deleteSharing(@PathVariable("id") Long id) {
    sharingService.delete(id);
  }
}
