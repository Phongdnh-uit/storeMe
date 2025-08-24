package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.permission.PermissionRequestDTO;
import com.DPhong.storeMe.dto.permission.PermissionResponseDTO;
import com.DPhong.storeMe.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper
    extends GenericMapper<Permission, PermissionRequestDTO, PermissionResponseDTO> {}
