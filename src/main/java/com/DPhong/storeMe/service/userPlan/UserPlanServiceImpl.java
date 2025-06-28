package com.DPhong.storeMe.service.userPlan;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.userPlan.UserPlanRequestDTO;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.entity.User;
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
  public UserPlanResponseDTO subscribe(UserPlanRequestDTO userPlanRequestDTO) {
    // 1. ---- Validate ----
    User user =
        userRepository
            .findById(userPlanRequestDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    StoragePlan storagePlan =
        storagePlanRepository
            .findById(userPlanRequestDTO.getStoragePlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Storage plan not found"));
    // 2. ---- Check if user already has an active plan ----
    Optional<UserPlan> optUserPlan = getCurrentUserPlanIfExists(user.getId());
    if (optUserPlan.isPresent()) {
      UserPlan userPlan = optUserPlan.get();
      // 3. ---- Check if user has same plan ----
      if (userPlan.getStoragePlan().getId() == userPlanRequestDTO.getStoragePlanId()) {
        return userPlanMapper.entityToResponse(userPlan);
      }
      // 4. ---- Inactive current plan ----
      userPlan.setActive(false);
      userPlanRepository.save(userPlan);
    }
    // 5. ---- Create new user plan ----
    UserPlan newUserPlan = new UserPlan();
    newUserPlan.setUser(user);
    newUserPlan.setStoragePlan(storagePlan);
    newUserPlan.setExpiredAt(Instant.now().plusSeconds(storagePlan.getTimeOfPlan() * 24 * 60 * 60));
    newUserPlan = userPlanRepository.save(newUserPlan);
    return userPlanMapper.entityToResponse(newUserPlan);
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
