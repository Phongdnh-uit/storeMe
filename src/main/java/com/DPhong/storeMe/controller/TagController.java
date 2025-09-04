package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.tag.TagRequestDTO;
import com.DPhong.storeMe.dto.tag.TagResponseDTO;
import com.DPhong.storeMe.entity.tag.Tag;
import com.DPhong.storeMe.service.tag.TagService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Quản lý thẻ của node")
@RequestMapping("/tags")
@RestController
public class TagController extends GenericController<Tag, TagRequestDTO, TagResponseDTO> {

  public TagController(TagService tagService) {
    super(tagService);
  }

  @Operation(summary = "Gán thẻ cho node")
  @PostMapping("{id}/fsNodes/{fsNodeId}")
  public ResponseEntity<ApiResponse<Void>> addTagToFsNode(
      @PathVariable("id") Long tagId, @PathVariable("fsNodeId") Long fsNodeId) {
    ((TagService) service).assignTagToFSNode(tagId, fsNodeId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Gỡ thẻ khỏi node")
  @PostMapping("{id}/fsNodes/{fsNodeId}/remove")
  public ResponseEntity<ApiResponse<Void>> removeTagFromFsNode(
      @PathVariable("id") Long tagId, @PathVariable("fsNodeId") Long fsNodeId) {
    ((TagService) service).removeTagFromFSNode(tagId, fsNodeId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
