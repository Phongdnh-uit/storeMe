package com.DPhong.storeMe.service.fsNode;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.fileSystemNode.CreateFolderRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.FSResponseDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UpdateFSNodeRequestDTO;
import com.DPhong.storeMe.dto.fileSystemNode.UploadFileRequestDTO;
import com.DPhong.storeMe.entity.FSNode;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface FSNodeService {

  PageResponse<FSResponseDTO> getAll(Long parentId, Specification<FSNode> spec, Pageable pageable);

  FSResponseDTO createFolder(CreateFolderRequestDTO request);

  List<FSResponseDTO> uploadFiles(UploadFileRequestDTO request);

  FSResponseDTO update(UpdateFSNodeRequestDTO request);

  void delete(Long id);

  void deleteMany(List<Long> ids);
}
