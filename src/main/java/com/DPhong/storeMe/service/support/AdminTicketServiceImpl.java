package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.dto.support.TicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.entity.TicketComment;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.support.AdminTicketMapper;
import com.DPhong.storeMe.mapper.support.TicketCommentMapper;
import com.DPhong.storeMe.repository.TicketCommentRepository;
import com.DPhong.storeMe.repository.TicketRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.GenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AdminTicketServiceImpl
    extends GenericService<Ticket, AdminTicketRequestDTO, TicketResponseDTO>
    implements AdminTicketService {
  private final UserRepository userRepository;
  private final TicketCommentMapper ticketCommentMapper;
  private final SecurityUtils securityUtils;
  private final TicketCommentRepository ticketCommentRepository;

  public AdminTicketServiceImpl(
      TicketRepository repository,
      AdminTicketMapper mapper,
      UserRepository userRepository,
      SecurityUtils securityUtils,
      TicketCommentRepository ticketCommentRepository,
      TicketCommentMapper ticketCommentMapper) {
    super(repository, mapper);
    this.userRepository = userRepository;
    this.ticketCommentMapper = ticketCommentMapper;
    this.securityUtils = securityUtils;
    this.ticketCommentRepository = ticketCommentRepository;
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

  // ============================ COMMENT ============================
  @Override
  public TicketCommentResponseDTO addCommentToTicket(
      Long ticketId, TicketCommentRequestDTO request) {
    if (!repository.existsById(ticketId)) {
      throw new ResourceNotFoundException("Comment not found");
    }
    TicketComment comment = ticketCommentMapper.requestToEntity(request);
    comment.setTicketId(ticketId);
    comment.setUserId(securityUtils.getCurrentUserId());
    comment = ticketCommentRepository.save(comment);
    return ticketCommentMapper.entityToResponse(comment);
  }

  @Override
  public TicketCommentResponseDTO updateComment(Long commentId, TicketCommentRequestDTO request) {
    TicketComment existingComment =
        ticketCommentRepository
            .findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    ticketCommentMapper.partialUpdate(request, existingComment);
    existingComment = ticketCommentRepository.save(existingComment);
    return ticketCommentMapper.entityToResponse(existingComment);
  }

  @Override
  public void deleteComment(Long commentId) {
    if (!ticketCommentRepository.existsById(commentId)) {
      throw new ResourceNotFoundException("Comment not found");
    }
    ticketCommentRepository.deleteById(commentId);
  }

  @Override
  public PageResponse<TicketCommentResponseDTO> getAllCommentInTicket(
      Long ticketId, Specification<TicketComment> spec, Pageable pageable) {
    Specification<TicketComment> specification =
        (root, _, builder) -> builder.and(builder.equal(root.get("ticketId"), ticketId));
    specification = specification.and(spec);
    Page<TicketComment> page = ticketCommentRepository.findAll(specification, pageable);
    return PageResponse.from(page.map(ticketCommentMapper::entityToResponse));
  }
}
