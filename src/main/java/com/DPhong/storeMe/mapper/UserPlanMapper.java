package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.userPlan.UserPlanRequestDTO;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.UserPlan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {StoragePlanMapper.class, UserMapper.class})
public interface UserPlanMapper
    extends GenericMapper<UserPlan, UserPlanRequestDTO, UserPlanResponseDTO> {}
