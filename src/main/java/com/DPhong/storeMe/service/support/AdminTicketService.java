package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.service.CrudService;

public interface AdminTicketService
    extends CrudService<Ticket, Long, AdminTicketRequestDTO, TicketResponseDTO> {}
