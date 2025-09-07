package com.DPhong.storeMe.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayPaymentRequestDTO {
  @NotBlank private Long invoiceId;
  private String bankCode;
  private String language;
  private String billingMobile;
  private String billingEmail;
  private String billingFullName;
  private String billingAddress;
  private String billingCity;
  private String billingCountry;
  private String billingState;

  private String invMobile;
  private String invEmail;
  private String invCustomer;
  private String invAddress1;
  private String invCompany;
  private String invTaxcode;
  private String invType;
}
