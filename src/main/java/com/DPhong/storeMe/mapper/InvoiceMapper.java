package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.invoice.InvoiceResponseDTO;
import com.DPhong.storeMe.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {
  InvoiceResponseDTO entityToResponse(Invoice invoice);
}
