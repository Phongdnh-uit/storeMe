package com.DPhong.storeMe.enums;

public enum InvoiceStatus {
  PENDING,
  PAID,
  FAILED,
  REFUNDED,
  CANCELED;

  public static InvoiceStatus fromString(String status) {
    try {
      return InvoiceStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown invoice status: " + status);
    }
  }
}
