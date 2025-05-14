package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.file.FileRequestDTO;
import com.DPhong.storeMe.dto.file.FileResponseDTO;
import com.DPhong.storeMe.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper extends GenericMapper<File, FileRequestDTO, FileResponseDTO> {

  @Override
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "folder.id", target = "folderId")
  FileResponseDTO entityToResponse(File entity);
}
