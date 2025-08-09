package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.InvoiceStatus;
import com.DPhong.storeMe.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {
  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long planId;

  @Column(nullable = false)
  private String planName;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Instant periodStart;

  @Column(nullable = false)
  private Instant periodEnd;

  @Column(nullable = false)
  private String txnRef;

  @Column(nullable = false)
  private Instant paidAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InvoiceStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod paymentMethod;
}
