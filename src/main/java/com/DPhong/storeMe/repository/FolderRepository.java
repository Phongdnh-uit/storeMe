package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.enums.FolderType;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends SimpleRepository<Folder, Long> {

  Optional<Folder> findByUserIdAndType(Long userId, FolderType type);
}
