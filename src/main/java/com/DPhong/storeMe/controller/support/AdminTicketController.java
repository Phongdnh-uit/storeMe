package com.DPhong.storeMe.controller.support;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.controller.GenericController;
import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.service.support.AdminTicketService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(AppConstant.BASE_URL + "/support/tickets/manage")
@RestController
public class AdminTicketController
    extends GenericController<Ticket, AdminTicketRequestDTO, TicketResponseDTO> {

  public AdminTicketController(AdminTicketService service) {
    super(service);
  }
}
