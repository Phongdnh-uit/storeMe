package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.support.TicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.service.CrudService;

public interface UserTicketService
    extends CrudService<Ticket, Long, TicketRequestDTO, TicketResponseDTO>, TicketCommentService {

  void closeTicket(Long id);
}
