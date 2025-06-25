package com.DPhong.storeMe.dto.fileSystemNode;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileRequestDTO extends FSRequestDTO {
  private List<MultipartFile> files = new ArrayList<>();
}
