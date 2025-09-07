package com.DPhong.storeMe.service.payment;

import com.DPhong.storeMe.dto.payment.VNPayPaymentRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
  String createVNPayPayment(VNPayPaymentRequestDTO request, HttpServletRequest servletRequest);

  Map<String, String> vnPayIPNCallback(Map<String, String> vnpParams);
}
