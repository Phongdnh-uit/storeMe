package com.DPhong.storeMe.controller.support;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.dto.support.TicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.support.Ticket;
import com.DPhong.storeMe.entity.support.TicketComment;
import com.DPhong.storeMe.service.support.UserTicketService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Ticket Management", description = "Quản lý vé hỗ trợ của người dùng")
@RequiredArgsConstructor
@RequestMapping(AppConstant.BASE_URL + "/support/tickets")
@RestController
public class UserTicketController {
  private final UserTicketService userTicketService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<TicketResponseDTO>>> getAll(
      @Filter Specification<Ticket> specification, @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(
        ApiResponse.success(userTicketService.findAll(specification, pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TicketResponseDTO>> getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(userTicketService.findById(id)));
  }

  @GetMapping("/{id}/comments")
  public ResponseEntity<ApiResponse<PageResponse<TicketCommentResponseDTO>>> getTicketComments(
      @PathVariable("id") Long id,
      @Filter Specification<TicketComment> spec,
      @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(
        ApiResponse.success(userTicketService.getAllCommentInTicket(id, spec, pageable)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TicketResponseDTO>> create(
      @Valid @RequestBody TicketRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(userTicketService.create(request)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TicketResponseDTO>> update(
      @PathVariable("id") Long id, @Valid @RequestBody TicketRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(userTicketService.update(id, request)));
  }

  @PatchMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable("id") Long id) {
    userTicketService.closeTicket(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/comment")
  public <AdminTicketService> ResponseEntity<ApiResponse<TicketCommentResponseDTO>> commentOnTicket(
      @PathVariable("id") Long id, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response = userTicketService.addCommentToTicket(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<TicketCommentResponseDTO>> updateTicketComment(
      @PathVariable("commentId") Long commentId, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response = userTicketService.updateComment(commentId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<Void>> deleteTicketComment(
      @PathVariable("commentId") Long commentId) {
    userTicketService.deleteComment(commentId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
