package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.role.RoleRequestDTO;
import com.DPhong.storeMe.dto.role.RoleResponseDTO;
import com.DPhong.storeMe.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GenericMapper<Role, RoleRequestDTO, RoleResponseDTO> {}
