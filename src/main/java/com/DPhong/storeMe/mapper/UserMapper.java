package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.user.UserRequestDTO;
import com.DPhong.storeMe.dto.user.UserResponseDTO;
import com.DPhong.storeMe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends GenericMapper<User, UserRequestDTO, UserResponseDTO> {

  @Override
  @Mapping(target = "passwordHash", ignore = true)
  void partialUpdate(UserRequestDTO request, @MappingTarget User entity);
}
