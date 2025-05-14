package com.DPhong.storeMe.service.file;

import com.DPhong.storeMe.dto.file.FileRequestDTO;
import com.DPhong.storeMe.dto.file.FileResponseDTO;
import java.util.List;
import org.springframework.core.io.Resource;

public interface FileService {

  FileResponseDTO getFileInfo(Long id);

  List<FileResponseDTO> uploadFile(FileRequestDTO fileRequestDTO);

  FileResponseDTO renameFile(Long id, String newName);

  Resource serveFile(Long id);

  void deleteFile(Long id);

  void deleteManyFiles(Iterable<Long> ids);
}
