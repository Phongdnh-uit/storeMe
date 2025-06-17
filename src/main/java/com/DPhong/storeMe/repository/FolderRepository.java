package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.enums.FolderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends SimpleRepository<Folder, Long> {

  Optional<Folder> findByUserIdAndType(Long userId, FolderType type);

  @Query(value = "SELECT * FROM folders f WHERE f.ancestor @> [:ancestorId]", nativeQuery = true)
  List<Folder> findByAncestorContain(@Param("ancestorId") Long ancestorId);
}
