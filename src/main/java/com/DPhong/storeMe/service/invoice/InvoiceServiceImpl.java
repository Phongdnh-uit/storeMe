package com.DPhong.storeMe.service.invoice;

import com.DPhong.storeMe.dto.invoice.InvoiceResponseDTO;
import com.DPhong.storeMe.dto.userPlan.SubscribeRequestDTO;
import com.DPhong.storeMe.entity.Invoice;
import com.DPhong.storeMe.entity.plan.StoragePlan;
import com.DPhong.storeMe.entity.plan.UserPlan;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.InvoiceStatus;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.InvoiceMapper;
import com.DPhong.storeMe.repository.InvoiceRepository;
import com.DPhong.storeMe.repository.StoragePlanRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.service.userPlan.UserPlanService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final StoragePlanRepository storagePlanRepository;
  private final InvoiceMapper invoiceMapper;
  private final UserPlanService userPlanService;

  @Override
  public InvoiceResponseDTO createInvoice(SubscribeRequestDTO userPlanRequestDTO) {
    // 1. ---- Validate ----
    Long userId = SecurityUtils.getCurrentUserId();
    // 1.1. ---- Check if user have invoice that not paid or payment fail ----
    if (invoiceRepository.exists(
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("userId"), userId),
                builder.or(
                    builder.equal(root.get("status"), InvoiceStatus.PENDING),
                    builder.equal(root.get("status"), InvoiceStatus.FAILED))))) {
      throw new ApiException(
          ErrorCode.VALIDATION_FAILED,
          "User has an unpaid invoice or payment failed. Please resolve it by cancelled before"
              + " subscribing to a new plan.");
    }
    StoragePlan storagePlan =
        storagePlanRepository
            .findById(userPlanRequestDTO.getStoragePlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Storage plan not found"));
    // 1.2. ---- Check if user already have an active plan ----
    Optional<UserPlan> optUserPlan = userPlanService.getCurrentUserPlanIfExists(userId);
    Invoice invoice = new Invoice();
    invoice.setPlanId(storagePlan.getId());
    invoice.setUserId(userId);
    invoice.setPlanName(storagePlan.getName());
    if (optUserPlan.isPresent()) {
      BigDecimal price = optUserPlan.get().getStoragePlan().getPrice();
      Long daysLeft =
          optUserPlan.get().getExpiredAt().toEpochMilli()
              - Instant.now().toEpochMilli() / (1000 * 60 * 60 * 24);
      BigDecimal pricePerDay = price.divide(BigDecimal.valueOf(storagePlan.getTimeOfPlan()));
      BigDecimal priceLeft = pricePerDay.multiply(BigDecimal.valueOf(daysLeft));
      invoice.setPrice(
          storagePlan.getPrice().subtract(priceLeft).setScale(2, RoundingMode.HALF_UP));
    } else {
      invoice.setPrice(storagePlan.getPrice());
    }
    invoice.setStatus(InvoiceStatus.PENDING);
    invoice.setPaymentMethod(userPlanRequestDTO.getPaymentMethod());
    invoice = invoiceRepository.save(invoice);
    return invoiceMapper.entityToResponse(invoice);
  }
}
