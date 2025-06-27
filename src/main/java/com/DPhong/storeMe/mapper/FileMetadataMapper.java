package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.fileSystemNode.FileMetadataResponseDTO;
import com.DPhong.storeMe.entity.FileMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMetadataMapper {
  FileMetadataResponseDTO entityToResponse(FileMetadata entity);
}
