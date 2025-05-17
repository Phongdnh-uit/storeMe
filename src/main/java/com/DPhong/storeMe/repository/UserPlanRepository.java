package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.UserPlan;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlanRepository extends SimpleRepository<UserPlan, Long> {
  Optional<UserPlan> findByUserIdAndIsActiveTrue(Long userId);
}
