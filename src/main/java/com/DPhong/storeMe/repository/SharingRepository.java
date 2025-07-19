package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Sharing;
import java.time.Instant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharingRepository extends SimpleRepository<Sharing, Long> {

  @Query(
      """
        UPDATE Sharing s
        SET s.updatedAt = :updatedAt
        WHERE s.id = :id
      """)
  Instant updateUpdatedAt(@Param("id") Long id, @Param("updatedAt") Instant updatedAt);
}
