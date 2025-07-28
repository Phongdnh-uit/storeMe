package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.support.AdminTicketMapper;
import com.DPhong.storeMe.repository.TicketRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.service.GenericService;
import org.springframework.stereotype.Service;

@Service
public class AdminTicketServiceImpl
    extends GenericService<Ticket, AdminTicketRequestDTO, TicketResponseDTO>
    implements AdminTicketService {
  private final UserRepository userRepository;

  public AdminTicketServiceImpl(
      TicketRepository repository, AdminTicketMapper mapper, UserRepository userRepository) {
    super(repository, mapper);
    this.userRepository = userRepository;
  }

  // ============================ OVERRIDE METHODS ============================
  @Override
  protected void beforeCreateMapper(AdminTicketRequestDTO request) {
    validateTicket(request);
  }

  @Override
  protected void beforeUpdateMapper(Long id, AdminTicketRequestDTO request, Ticket oldEntity) {
    validateTicket(request);
  }

  // ============================ HELPER METHODS ============================
  void validateTicket(TicketRequestDTO request) {
    if (!userRepository.existsById(request.getUserId())) {
      throw new ResourceNotFoundException("User not found");
    }
  }
}
