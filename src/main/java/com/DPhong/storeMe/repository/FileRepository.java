package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.File;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends SimpleRepository<File, Long> {

  @Query(value = "SELECT * FROM files f WHERE f.ancestor @> [:ancestorId]", nativeQuery = true)
  List<File> findByAncestorContain(@Param("ancestorId") Long ancestorId);
}
