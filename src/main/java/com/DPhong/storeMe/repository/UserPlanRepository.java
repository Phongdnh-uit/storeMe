package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.plan.UserPlan;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlanRepository extends SimpleRepository<UserPlan, Long> {}
