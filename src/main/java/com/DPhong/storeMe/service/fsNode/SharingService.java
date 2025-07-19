package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.sharing.CreateSharingRequestDTO;
import com.DPhong.storeMe.dto.sharing.SharingResponseDTO;
import com.DPhong.storeMe.dto.sharing.UpdateSharingRequestDTO;
import com.DPhong.storeMe.entity.Sharing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SharingService {
  PageResponse<SharingResponseDTO> getAllSharedByMe(Specification<Sharing> spec, Pageable pageable);

  PageResponse<SharingResponseDTO> getAllSharedWithMe(
      Specification<Sharing> spec, Pageable pageable);

  SharingResponseDTO create(Long fsNodeId, CreateSharingRequestDTO createSharingRequestDTO);

  SharingResponseDTO update(Long id, UpdateSharingRequestDTO updateSharingRequestDTO);

  void delete(Long id);
}
