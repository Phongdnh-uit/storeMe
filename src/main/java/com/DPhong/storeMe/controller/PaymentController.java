package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.payment.VNPayPaymentRequestDTO;
import com.DPhong.storeMe.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/payment")
@RequiredArgsConstructor
@RestController
public class PaymentController {
  private final PaymentService paymentService;

  @GetMapping("/vnpay/create-payment")
  public ResponseEntity<ApiResponse<String>> createVNPayPayment(
      @Valid @RequestBody VNPayPaymentRequestDTO request, HttpServletRequest servletRequest) {
    return ResponseEntity.ok(
        ApiResponse.success(paymentService.createVNPayPayment(request, servletRequest)));
  }

  @GetMapping("/vnpay/ipn")
  public ResponseEntity<Map<String, String>> vnpayIpn(HttpServletRequest request) {
    try {
      Map<String, String> fields = new HashMap<>();
      for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
        String fieldName =
            URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
        String fieldValue =
            URLEncoder.encode(
                request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
        if ((fieldValue != null) && (fieldValue.length() > 0)) {
          fields.put(fieldName, fieldValue);
        }
      }
      return ResponseEntity.ok(paymentService.vnPayIPNCallback(fields));
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("RspCode", "99");
      response.put("Message", "Unknown error");
      return ResponseEntity.ok(response);
    }
  }
}
