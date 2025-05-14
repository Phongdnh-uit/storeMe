package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.storagePlan.StoragePlanRequestDTO;
import com.DPhong.storeMe.dto.storagePlan.StoragePlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoragePlanMapper
    extends GenericMapper<StoragePlan, StoragePlanRequestDTO, StoragePlanResponseDTO> {}
