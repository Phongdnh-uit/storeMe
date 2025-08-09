package com.DPhong.storeMe.service.userPlan;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.Invoice;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.entity.UserPlan;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.UserPlanMapper;
import com.DPhong.storeMe.repository.StoragePlanRepository;
import com.DPhong.storeMe.repository.UserPlanRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserPlanServiceImpl implements UserPlanService {
  private final UserPlanMapper userPlanMapper;
  private final StoragePlanRepository storagePlanRepository;
  private final UserRepository userRepository;
  private final UserPlanRepository userPlanRepository;
  private final SecurityUtils securityUtils;

  // ============================ SUBSCRIBE OR UPGRADE PLAN ============================

  @Override
  public void createUserPlanAfterPayment(Invoice invoice) {
    Optional<UserPlan> optUserPlan = getCurrentUserPlanIfExists(invoice.getUserId());
    if (optUserPlan.isPresent()) {
      optUserPlan.get().setActive(false);
      optUserPlan.get().setExpiredAt(Instant.now());
      userPlanRepository.save(optUserPlan.get());
    }
    StoragePlan storagePlan =
        storagePlanRepository
            .findById(invoice.getPlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Storage plan not found"));
    UserPlan userPlan = new UserPlan();
    userPlan.setUser(userRepository.getReferenceById(invoice.getUserId()));
    userPlan.setStoragePlan(storagePlan);
    userPlan.setActive(true);
    userPlan.setExpiredAt(invoice.getPeriodEnd());
    userPlanRepository.save(userPlan);
  }

  // ============================ GET CURRENT PLAN ============================
  @Override
  public UserPlanResponseDTO getCurrentPlan() {
    Long userId = securityUtils.getCurrentUserId();
    Optional<UserPlan> optUserPlan = getCurrentUserPlanIfExists(userId);
    if (optUserPlan.isEmpty()) {
      throw new ResourceNotFoundException("Current user does not have an active plan");
    }
    return userPlanMapper.entityToResponse(optUserPlan.get());
  }

  // ============================ GET USER PLAN HISTORY ============================
  @Override
  public PageResponse<UserPlanResponseDTO> getUserPlanHistory(
      Specification<UserPlan> specification, Pageable pageable) {
    Long userId = securityUtils.getCurrentUserId();
    Specification<UserPlan> userPlanSpec =
        (root, _, builder) -> builder.and(builder.equal(root.get("user").get("id"), userId));
    userPlanSpec.and(specification);
    Page<UserPlan> page = userPlanRepository.findAll(userPlanSpec, pageable);
    return PageResponse.from(page.map(userPlanMapper::entityToResponse));
  }

  // ============================ CANCEL CURRENT PLAN ============================
  @Override
  public void cancelCurrentPlan() {
    Long userId = securityUtils.getCurrentUserId();
    Optional<UserPlan> optUserPlan = getCurrentUserPlanIfExists(userId);
    if (optUserPlan.isEmpty()) {
      throw new ResourceNotFoundException("Current user does not have an active plan");
    }
    UserPlan userPlan = optUserPlan.get();
    userPlan.setActive(false);
    userPlanRepository.save(userPlan);
  }

  @Override
  public Optional<UserPlan> getCurrentUserPlanIfExists(Long userId) {
    Optional<UserPlan> optUserPlan =
        userPlanRepository.findOne(
            (root, _, builder) ->
                builder.and(
                    builder.equal(root.get("user").get("id"), userId),
                    builder.equal(root.get("isActive"), true)));
    if (optUserPlan.isPresent() && optUserPlan.get().getExpiredAt().isBefore(Instant.now())) {
      return Optional.empty();
    }
    return optUserPlan;
  }
}
