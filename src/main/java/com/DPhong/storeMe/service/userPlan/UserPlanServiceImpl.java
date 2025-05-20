package com.DPhong.storeMe.service.userPlan;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.userPlan.UserPlanRequestDTO;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.entity.UserPlan;
import com.DPhong.storeMe.exception.BadRequestException;
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

  @Override
  public UserPlanResponseDTO subscribe(UserPlanRequestDTO userPlanRequestDTO) {
    User user =
        userRepository
            .findById(securityUtils.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    StoragePlan storagePlan =
        storagePlanRepository
            .findById(userPlanRequestDTO.getStoragePlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Storage plan not found"));
    Optional<UserPlan> currentPlan = userPlanRepository.findByUserIdAndIsActiveTrue(user.getId());
    if (currentPlan.isPresent()) {
      if (currentPlan.get().getStoragePlan().getId() == storagePlan.getId()) {
        throw new BadRequestException("You already have this plan");
      }
      UserPlan userPlan = currentPlan.get();
      userPlan.setActive(false);
      userPlanRepository.save(userPlan);
    }
    UserPlan userPlan = new UserPlan();
    userPlan.setUser(user);
    userPlan.setStoragePlan(storagePlan);
    userPlan.setActive(true);
    userPlan.setExpiredAt(Instant.now().plusSeconds(storagePlan.getTimeOfPlan() * 24 * 60 * 60));
    userPlan.setAssignedAt(Instant.now());
    return userPlanMapper.entityToResponse(userPlanRepository.save(userPlan));
  }

  @Override
  public UserPlanResponseDTO getCurrentPlan() {
    return userPlanMapper.entityToResponse(getCurrentUserPlan());
  }

  @Override
  public PageResponse<UserPlanResponseDTO> getUserPlanHistory(
      Specification<UserPlan> specification, Pageable pageable) {
    Long userId = securityUtils.getCurrentUserId();
    Specification<UserPlan> spec =
        (root, _, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    spec.and(specification);
    Page<UserPlan> page = userPlanRepository.findAll(spec, pageable);
    return new PageResponse<UserPlanResponseDTO>()
        .setContent(
            page.getContent().stream().map(v -> userPlanMapper.entityToResponse(v)).toList())
        .setNumber(page.getNumber())
        .setSize(page.getSize())
        .setTotalElements(page.getTotalElements())
        .setTotalPages(page.getTotalPages());
  }

  @Override
  public void cancelCurrentPlan() {
    UserPlan current = getCurrentUserPlan();
    current.setActive(false);
    userPlanRepository.save(current);
  }

  private UserPlan getCurrentUserPlan() {
    Long userId = securityUtils.getCurrentUserId();
    UserPlan current =
        userPlanRepository
            .findByUserIdAndIsActiveTrue(userId)
            .orElseThrow(
                () -> new BadRequestException("No active plan found for user with id: " + userId));
    if (current.getExpiredAt().isBefore(Instant.now())) {
      current.setActive(false);
      userPlanRepository.save(current);
      throw new BadRequestException("Your plan has expired");
    }
    return current;
  }
}
