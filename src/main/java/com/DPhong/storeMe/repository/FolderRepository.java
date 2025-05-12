package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Folder;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends SimpleRepository<Folder, Long> {
  boolean existsByNameAndParentFolderId(String name, Long parentId);
}
