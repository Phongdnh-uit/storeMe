package com.DPhong.storeMe.config;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.ErrorVO;
import com.DPhong.storeMe.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    ErrorVO errorVO = new ErrorVO();

    // 1. ---- Get root cause ----
    Throwable cause = authException.getCause();
    while (cause != null && cause.getCause() != null) {
      cause = cause.getCause();
    }
    if (cause instanceof JwtValidationException jwtValidationException) {
      boolean isExpired = jwtValidationException.getMessage().contains("expired");
      if (isExpired) {
        errorVO.setErrorCode(ErrorCode.TOKEN_EXPIRED.getCode());
        errorVO.setErrorMessage(ErrorCode.TOKEN_EXPIRED.getMessage());
      } else {
        errorVO.setErrorCode(ErrorCode.INVALID_TOKEN.getCode());
        errorVO.setErrorMessage(ErrorCode.INVALID_TOKEN.getMessage());
      }
    } else {
      errorVO.setErrorCode(ErrorCode.AUTH_FAILED.getCode());
      errorVO.setErrorMessage(ErrorCode.AUTH_FAILED.getMessage());
    }
    // 2. ---- Set response body ----
    ApiResponse<Void> apiResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorVO);

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // 3. ---- Write response body ----
    objectMapper.writeValue(response.getOutputStream(), apiResponse);
  }
}
