package com.DPhong.storeMe.service.userPlan;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.userPlan.UserPlanRequestDTO;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.UserPlan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserPlanService {

  /** register or upgrade a plan */
  UserPlanResponseDTO subscribe(UserPlanRequestDTO userPlanRequestDTO);

  UserPlanResponseDTO getCurrentPlan();

  PageResponse<UserPlanResponseDTO> getUserPlanHistory(
      Specification<UserPlan> specification, Pageable pageable);

  void cancelCurrentPlan();
}
