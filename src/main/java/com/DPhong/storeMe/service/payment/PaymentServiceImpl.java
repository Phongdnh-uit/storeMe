package com.DPhong.storeMe.service.payment;

import com.DPhong.storeMe.config.VNPayConfig;
import com.DPhong.storeMe.dto.payment.VNPayPaymentRequestDTO;
import com.DPhong.storeMe.entity.Invoice;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.enums.InvoiceStatus;
import com.DPhong.storeMe.exception.ApiException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.repository.InvoiceRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final VNPayConfig config;
  private final SecurityUtils securityUtils;
  private final InvoiceRepository invoiceRepository;

  public String createVNPayPayment(
      VNPayPaymentRequestDTO request, HttpServletRequest servletRequest) {

    Long userId = securityUtils.getCurrentUserId();
    Invoice invoice =
        invoiceRepository
            .findOne(
                (root, _, builder) ->
                    builder.and(
                        builder.equal(root.get("id"), request.getInvoiceId()),
                        builder.equal(root.get("userId"), userId),
                        builder.or(builder.equal(root.get("status"), InvoiceStatus.PENDING))))
            .orElseThrow(() -> new ResourceNotFoundException("No invoice needs to be paid"));

    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String vnp_OrderInfo = "Thanh toan hoa don " + invoice.getId() + " - storeMe";
    String orderType = "250000";
    String vnp_TxnRef = "INV-" + SecurityUtils.generateRandomNumber();
    invoice.setTxnRef(vnp_TxnRef);
    String vnp_IpAddr = SecurityUtils.getClientIp(servletRequest);
    String vnp_TmnCode = config.getVnpTmnCode();

    BigDecimal amount = invoice.getPrice();
    String vnp_Amount =
        amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).toPlainString();
    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", vnp_Version);
    vnp_Params.put("vnp_Command", vnp_Command);
    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
    vnp_Params.put("vnp_Amount", vnp_Amount);
    vnp_Params.put("vnp_CurrCode", "VND");
    String bank_code = request.getBankCode();
    if (bank_code != null && !bank_code.isEmpty()) {
      vnp_Params.put("vnp_BankCode", bank_code);
    }
    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
    vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
    vnp_Params.put("vnp_OrderType", orderType);

    String locate = request.getLanguage();
    if (locate != null && !locate.isEmpty()) {
      vnp_Params.put("vnp_Locale", locate);
    } else {
      vnp_Params.put("vnp_Locale", "vn");
    }
    vnp_Params.put("vnp_ReturnUrl", config.getVnpReturnUrl());
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());

    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
    cld.add(Calendar.MINUTE, 15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
    // Add Params of 2.1.0 Version
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
    // Billing
    vnp_Params.put("vnp_Bill_Mobile", request.getBillingMobile());
    vnp_Params.put("vnp_Bill_Email", request.getBillingEmail());
    String fullName = (request.getBillingFullName()).trim();
    if (fullName != null && !fullName.isEmpty()) {
      int idx = fullName.indexOf(' ');
      String firstName = fullName.substring(0, idx);
      String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
      vnp_Params.put("vnp_Bill_FirstName", firstName);
      vnp_Params.put("vnp_Bill_LastName", lastName);
    }
    vnp_Params.put("vnp_Bill_Address", request.getBillingAddress());
    vnp_Params.put("vnp_Bill_City", request.getBillingCity());
    vnp_Params.put("vnp_Bill_Country", request.getBillingCountry());
    if (request.getBillingState() != null && !request.getBillingState().isEmpty()) {
      vnp_Params.put("vnp_Bill_State", request.getBillingState());
    }
    // Invoice
    vnp_Params.put("vnp_Inv_Phone", request.getInvMobile());
    vnp_Params.put("vnp_Inv_Email", request.getInvEmail());
    vnp_Params.put("vnp_Inv_Customer", request.getInvCustomer());
    vnp_Params.put("vnp_Inv_Address", request.getInvAddress1());
    vnp_Params.put("vnp_Inv_Company", request.getInvCompany());
    vnp_Params.put("vnp_Inv_Taxcode", request.getInvTaxcode());
    vnp_Params.put("vnp_Inv_Type", request.getInvType());
    // Build data to hash and querystring
    List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = vnp_Params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        // Build hash data
        try {

          hashData.append(fieldName);
          hashData.append('=');
          hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
          // Build query
          query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
          query.append('=');
          query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
          if (itr.hasNext()) {
            query.append('&');
            hashData.append('&');
          }
        } catch (Exception e) {
          throw new ApiException(
              ErrorCode.UNEXPECTED_ERROR,
              "Error encoding field: " + fieldName + " with value: " + fieldValue);
        }
      }
    }
    String queryUrl = query.toString();
    String vnp_SecureHash = VNPayConfig.hmacSHA512(config.getVnpHashSecret(), hashData.toString());
    queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
    String paymentUrl = config.getVnpPayUrl() + "?" + queryUrl;
    invoiceRepository.save(invoice);
    return paymentUrl;
  }

  public Map<String, String> vnPayIPNCallback(Map<String, String> vnpParams) {
    Map<String, String> response = new HashMap<>();
    // 1. ---- Compare checksum ----
    String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
    if (vnpParams.containsKey("vnp_SecureHashType")) {
      vnpParams.remove("vnp_SecureHashType");
    }
    if (vnpParams.containsKey("vnp_SecureHash")) {
      vnpParams.remove("vnp_SecureHash");
    }

    List<String> fieldNames =
        vnpParams.keySet().stream().filter(key -> key != null && !key.isEmpty()).sorted().toList();

    StringBuilder hashData = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = URLEncoder.encode(vnpParams.get(fieldName), StandardCharsets.US_ASCII);
      if (fieldValue != null && !fieldValue.isEmpty()) {
        hashData.append(fieldName).append("=").append(fieldValue);
      }
      if (itr.hasNext()) {
        hashData.append("&");
      }
    }

    String signValue = VNPayConfig.hmacSHA512(config.getVnpHashSecret(), hashData.toString());

    if (!signValue.equals(vnp_SecureHash)) {
      response.put("RspCode", "97");
      response.put("Message", "Invalid Checksum");
      return response;
    }

    String vnp_TxnRef = vnpParams.get("vnp_TxnRef");
    String vnp_Amount = vnpParams.get("vnp_Amount");

    Optional<Invoice> invoiceOpt =
        invoiceRepository.findOne(
            (root, _, builder) -> builder.and(builder.equal(root.get("txnRef"), vnp_TxnRef)));

    // 2. ---- Check invoice exists ----
    if (!invoiceOpt.isPresent()) {
      response.put("RspCode", "01");
      response.put("Message", "Order not found");
      return response;
    }

    // 3. ---- Check amount and status ----
    if (invoiceOpt
            .get()
            .getPrice()
            .multiply(new BigDecimal("100"))
            .compareTo(new BigDecimal(vnp_Amount))
        != 0) {
      response.put("RspCode", "04");
      response.put("Message", "Invalid Amount");
      return response;
    }

    if (invoiceOpt.get().getStatus() != InvoiceStatus.PENDING) {
      response.put("RspCode", "02");
      response.put("Message", "Order already confirmed");
      return response;
    }

    if ("00".equals(vnpParams.get("vnp_ResponseCode"))) {
      invoiceOpt.get().setStatus(InvoiceStatus.PAID);
    } else {
      invoiceOpt.get().setStatus(InvoiceStatus.FAILED);
    }
    response.put("RspCode", "00");
    response.put("Message", "Confirm Success");
    invoiceRepository.save(invoiceOpt.get());
    return response;
  }
}
