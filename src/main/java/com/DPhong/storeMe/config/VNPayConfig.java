package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.exception.ApiException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class VNPayConfig {

  @Value("${vnp.tmn-code}")
  private String vnpTmnCode;

  @Value("${vnp.pay-url}")
  private String vnpPayUrl;

  @Value("${vnp.hash-secret}")
  private String vnpHashSecret;

  private final String vnpReturnUrl = AppConstant.BACKEND_URL + "payment/vnpay/return";

  public static String hmacSHA512(String key, String data) {
    try {
      Mac mac = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");

      mac.init(secretKeySpec);

      byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

      StringBuilder hexString = new StringBuilder();
      for (byte b : hmacBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new ApiException(
          ErrorCode.INTERNAL_ERROR, "Error generating HMAC SHA-512: " + e.getMessage());
    }
  }
}
