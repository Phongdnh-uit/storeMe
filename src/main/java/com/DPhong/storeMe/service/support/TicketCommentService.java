package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.entity.TicketComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TicketCommentService {
  PageResponse<TicketCommentResponseDTO> getAllCommentInTicket(
      Long ticketId, Specification<TicketComment> spec, Pageable pageable);

  public TicketCommentResponseDTO addCommentToTicket(
      Long ticketId, TicketCommentRequestDTO request);

  public TicketCommentResponseDTO updateComment(Long commentId, TicketCommentRequestDTO request);

  public void deleteComment(Long commentId);
}
