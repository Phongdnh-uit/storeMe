package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.sharing.SharingResponseDTO;
import com.DPhong.storeMe.entity.sharing.Sharing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SharingMapper {
  SharingResponseDTO entityToResponse(Sharing sharing);
}
