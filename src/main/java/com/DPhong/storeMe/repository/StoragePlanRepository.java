package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.plan.StoragePlan;
import org.springframework.stereotype.Repository;

@Repository
public interface StoragePlanRepository extends SimpleRepository<StoragePlan, Long> {
  boolean existsByName(String name);
}
