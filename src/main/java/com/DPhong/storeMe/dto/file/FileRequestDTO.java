package com.DPhong.storeMe.dto.file;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileRequestDTO {
  private Long folderId;
  List<MultipartFile> files = new ArrayList<>();
}
