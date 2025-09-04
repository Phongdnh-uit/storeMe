package com.DPhong.storeMe.controller.support;

import com.DPhong.storeMe.controller.GenericController;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserTicket", description = "Quản lý vé hỗ trợ của người dùng")
@RequestMapping("/support/tickets")
@RestController
public class UserTicketController
    extends GenericController<Ticket, TicketRequestDTO, TicketResponseDTO> {

  public UserTicketController(UserTicketService service) {
    super(service);
  }

  @Operation(summary = "Lấy các comment trong vé hỗ trợ")
  @GetMapping("/{id}/comments")
  public ResponseEntity<ApiResponse<PageResponse<TicketCommentResponseDTO>>> getTicketComments(
      @PathVariable("id") Long id,
      @Filter Specification<TicketComment> spec,
      @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(
        ApiResponse.success(
            ((UserTicketService) service).getAllCommentInTicket(id, spec, pageable)));
  }

  @Operation(summary = "Đóng vé hỗ trợ")
  @PatchMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable("id") Long id) {
    ((UserTicketService) service).closeTicket(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "Thêm comment vào vé hỗ trợ")
  @PostMapping("/{id}/comment")
  public <AdminTicketService> ResponseEntity<ApiResponse<TicketCommentResponseDTO>> commentOnTicket(
      @PathVariable("id") Long id, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response =
        ((UserTicketService) service).addCommentToTicket(id, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "Cập nhật comment trong vé hỗ trợ")
  @PutMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<TicketCommentResponseDTO>> updateTicketComment(
      @PathVariable("commentId") Long commentId, TicketCommentRequestDTO request) {
    TicketCommentResponseDTO response =
        ((UserTicketService) service).updateComment(commentId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "Xoá comment trong vé hỗ trợ")
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<Void>> deleteTicketComment(
      @PathVariable("commentId") Long commentId) {
    ((UserTicketService) service).deleteComment(commentId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
