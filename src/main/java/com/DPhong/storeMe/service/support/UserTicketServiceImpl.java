package com.DPhong.storeMe.service.support;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.dto.support.TicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.Ticket;
import com.DPhong.storeMe.entity.TicketComment;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.TicketStatus;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.support.TicketCommentMapper;
import com.DPhong.storeMe.mapper.support.UserTicketMapper;
import com.DPhong.storeMe.repository.TicketCommentRepository;
import com.DPhong.storeMe.repository.TicketRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.GenericService;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserTicketServiceImpl
    extends GenericService<Ticket, TicketRequestDTO, TicketResponseDTO>
    implements UserTicketService {

  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;
  private final TicketCommentRepository ticketCommentRepository;
  private final TicketCommentMapper ticketCommentMapper;

  public UserTicketServiceImpl(
      TicketRepository repository,
      UserTicketMapper mapper,
      SecurityUtils securityUtils,
      TicketCommentRepository ticketCommentRepository,
      TicketCommentMapper ticketCommentMapper,
      UserRepository userRepository) {
    super(repository, mapper);
    this.userRepository = userRepository;
    this.securityUtils = securityUtils;
    this.ticketCommentRepository = ticketCommentRepository;
    this.ticketCommentMapper = ticketCommentMapper;
  }

  @Override
  public PageResponse<TicketResponseDTO> findAll(
      Specification<Ticket> specification, Pageable pageable) {
    Specification<Ticket> userSpec =
        (root, _, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("userId"), securityUtils.getCurrentUserId());
    userSpec = userSpec.and(specification);
    return super.findAll(userSpec, pageable);
  }

  @Override
  public TicketResponseDTO findById(Long id) {
    Optional<Ticket> ticket =
        repository.findOne(
            (root, _, criteriaBuilder) ->
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("id"), id),
                    criteriaBuilder.equal(root.get("userId"), securityUtils.getCurrentUserId())));
    if (ticket.isEmpty()) {
      throw new ResourceNotFoundException(" ticket not found");
    }
    return mapper.entityToResponse(ticket.get());
  }

  @Override
  protected void beforeCreateMapper(TicketRequestDTO request) {
    validateUserExists(request.getUserId());
  }

  @Override
  protected void beforeUpdateMapper(Long id, TicketRequestDTO request, Ticket oldEntity) {
    if (oldEntity.getStatus() != TicketStatus.OPEN) {
      throw new ApiException(
          ErrorCode.VALIDATION_FAILED, "Only tickets with status OPEN can be updated.");
    }
    validateUserExists(request.getUserId());
  }

  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException(
        "Delete operation is not supported for user support tickets.");
  }

  @Override
  public void deleteAllById(Iterable<Long> ids) {
    throw new UnsupportedOperationException(
        "Delete operation is not supported for user support tickets.");
  }

  // ============================ CLOSE TICKET ============================
  @Override
  public void closeTicket(Long id) {
    Ticket ticket =
        repository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), id),
                        builder.equal(root.get("userId"), securityUtils.getCurrentUserId())))
            .orElseThrow(() -> new ResourceNotFoundException(" ticket not found"));
    if (ticket.getStatus() != TicketStatus.OPEN) {
      throw new ApiException(
          ErrorCode.VALIDATION_FAILED, "Only tickets with status OPEN can be closed.");
    }
    ticket.setStatus(TicketStatus.CLOSED);
    repository.save(ticket);
  }

  // ============================ HELPER METHODS ============================
  void validateUserExists(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found");
    }
  }

  @Override
  public TicketCommentResponseDTO addCommentToTicket(
      Long ticketId, TicketCommentRequestDTO request) {
    Long userId = securityUtils.getCurrentUserId();
    if (!repository.exists(
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("id"), ticketId),
                builder.equal(root.get("userId"), userId)))) {
      throw new ResourceNotFoundException("Ticket not found or does not belong to the user");
    }
    TicketComment comment = ticketCommentMapper.requestToEntity(request);
    comment.setTicketId(ticketId);
    comment.setUserId(userId);
    comment = ticketCommentRepository.save(comment);
    return ticketCommentMapper.entityToResponse(comment);
  }

  @Override
  public TicketCommentResponseDTO updateComment(Long commentId, TicketCommentRequestDTO request) {
    TicketComment comment =
        ticketCommentRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), commentId),
                        builder.equal(root.get("userId"), securityUtils.getCurrentUserId())))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Comment not found or does not belong to the user"));
    ticketCommentMapper.partialUpdate(request, comment);
    comment = ticketCommentRepository.save(comment);
    return ticketCommentMapper.entityToResponse(comment);
  }

  @Override
  public void deleteComment(Long commentId) {
    if (!ticketCommentRepository.exists(
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("id"), commentId),
                builder.equal(root.get("userId"), securityUtils.getCurrentUserId())))) {
      throw new ResourceNotFoundException("Comment not found or does not belong to the user");
    }
    ticketCommentRepository.deleteById(commentId);
  }
}
