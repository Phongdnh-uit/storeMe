package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Folder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends SimpleRepository<Folder, Long> {

  Optional<Folder> findByUserIdAndParentIdIsNull(Long userId);

  Page<Folder> findAllByUserIdAndParentFolderIdIsNull(
      Long userId, Specification<Folder> specification, Pageable pageable);
}
