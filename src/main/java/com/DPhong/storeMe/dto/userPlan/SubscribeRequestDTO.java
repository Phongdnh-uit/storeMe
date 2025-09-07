package com.DPhong.storeMe.dto.userPlan;

import com.DPhong.storeMe.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeRequestDTO {
  // @NotNull(message = "user id is required")
  // private Long userId;

  @NotNull(message = "storage plan id is required")
  private Long storagePlanId;

  @NotNull(message = "payment method is required")
  private PaymentMethod paymentMethod;
}
