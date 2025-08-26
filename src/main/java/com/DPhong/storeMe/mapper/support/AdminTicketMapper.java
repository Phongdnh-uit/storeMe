package com.DPhong.storeMe.mapper.support;

import com.DPhong.storeMe.dto.support.AdminTicketRequestDTO;
import com.DPhong.storeMe.dto.support.TicketResponseDTO;
import com.DPhong.storeMe.entity.support.Ticket;
import com.DPhong.storeMe.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminTicketMapper
    extends GenericMapper<Ticket, AdminTicketRequestDTO, TicketResponseDTO> {

  @Mapping(target = "userId", source = "userId", ignore = true)
  @Override
  void partialUpdate(AdminTicketRequestDTO request, @MappingTarget Ticket entity);
}
