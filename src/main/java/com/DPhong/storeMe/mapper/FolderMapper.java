package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.dto.folder.FolderResponseDTO;
import com.DPhong.storeMe.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {UserMapper.class, FileMapper.class})
public interface FolderMapper extends GenericMapper<Folder, FolderRequestDTO, FolderResponseDTO> {

    @Override
    @Mapping(source = "parentFolder.id", target = "parentFolderId")
    FolderResponseDTO entityToResponse(Folder entity);
}
