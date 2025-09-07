package com.DPhong.storeMe.dto.invoice;

import com.DPhong.storeMe.entity.BaseEntity;
import com.DPhong.storeMe.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponseDTO extends BaseEntity {
  private Long userId;

  private Long planId;

  private String planName;

  private BigDecimal price;

  private Instant periodStart;

  private Instant periodEnd;

  // private String txnRef;

  private Instant paidAt;

  private InvoiceStatus status;
}
