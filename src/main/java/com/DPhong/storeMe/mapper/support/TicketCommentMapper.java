package com.DPhong.storeMe.mapper.support;

import com.DPhong.storeMe.dto.support.TicketCommentRequestDTO;
import com.DPhong.storeMe.dto.support.TicketCommentResponseDTO;
import com.DPhong.storeMe.entity.TicketComment;
import com.DPhong.storeMe.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketCommentMapper
    extends GenericMapper<TicketComment, TicketCommentRequestDTO, TicketCommentResponseDTO> {}
