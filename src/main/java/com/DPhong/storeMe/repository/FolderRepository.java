package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Folder;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends SimpleRepository<Folder, Long> {

  Optional<Folder> findByUserIdAndParentFolderIdIsNull(Long userId);
}
