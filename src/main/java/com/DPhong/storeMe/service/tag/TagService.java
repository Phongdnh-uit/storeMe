package com.DPhong.storeMe.service.tag;

import com.DPhong.storeMe.dto.tag.TagRequestDTO;
import com.DPhong.storeMe.dto.tag.TagResponseDTO;
import com.DPhong.storeMe.entity.tag.Tag;
import com.DPhong.storeMe.service.CrudService;

public interface TagService extends CrudService<Tag, Long, TagRequestDTO, TagResponseDTO> {
  void assignTagToFSNode(Long tagId, Long fsNodeId);

  void removeTagFromFSNode(Long tagId, Long fsNodeId);
}
