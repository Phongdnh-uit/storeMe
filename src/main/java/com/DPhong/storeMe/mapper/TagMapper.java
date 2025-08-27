package com.DPhong.storeMe.mapper;

import com.DPhong.storeMe.dto.tag.TagRequestDTO;
import com.DPhong.storeMe.dto.tag.TagResponseDTO;
import com.DPhong.storeMe.entity.tag.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper extends GenericMapper<Tag, TagRequestDTO, TagResponseDTO> {}
