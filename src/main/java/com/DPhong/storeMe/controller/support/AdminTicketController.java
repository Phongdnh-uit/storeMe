package com.DPhong.storeMe.controller.support;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.controller.GenericController;
import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.support.Ticket;
import com.DPhong.storeMe.entity.support.TicketComment;
import com.DPhong.storeMe.service.support.AdminTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "AdminTicket", description = "Quản lý vé hỗ trợ cho quản trị viên")
@RequestMapping("/support/tickets/manage")
@RestController
public class AdminTicketController
    extends GenericController<Ticket, AdminTicketRequestDTO, TicketResponseDTO> {

  public AdminTicketController(AdminTicketService service) {
    super(service);
  }

  @GetMapping("/{id}/comments")
  public ResponseEntity<ApiResponse<PageResponse<TicketCommentResponseDTO>>> getTicketComments(
      @PathVariable("id") Long id,
      Specification<TicketComment> spec,
      @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(
        ApiResponse.success(
            ((AdminTicketService) service).getAllCommentInTicket(id, spec, pageable)));
  }

  @PostMapping("/{id}/comment")
  public ResponseEntity<ApiResponse<TicketCommentResponseDTO>> commentOnTicket(
      @PathVariable("id") Long id, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response =
        ((AdminTicketService) service).addCommentToTicket(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<TicketCommentResponseDTO>> updateTicketComment(
      @PathVariable("commentId") Long commentId, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response =
        ((AdminTicketService) service).updateComment(commentId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<Void>> deleteTicketComment(
      @PathVariable("commentId") Long commentId) {
    ((AdminTicketService) service).deleteComment(commentId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
