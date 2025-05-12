package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FolderMapper extends GenericMapper<Folder, FolderRequestDTO, FolderResponseDTO> {}
