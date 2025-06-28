package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.entity.FSNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {FileMetadataMapper.class})
public interface FSNodeMapper {

  /**
   * Maps a DTO to an entity.
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "parent.id", target = "parentId")
  FSResponseDTO entityToResponse(FSNode entity);
}
