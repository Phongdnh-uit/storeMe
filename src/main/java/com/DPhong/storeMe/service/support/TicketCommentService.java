package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;

public interface TicketCommentService {
  public TicketCommentResponseDTO addCommentToTicket(
      Long ticketId, TicketCommentRequestDTO request);

  public TicketCommentResponseDTO updateComment(Long commentId, TicketCommentRequestDTO request);

  public void deleteComment(Long commentId);
}
