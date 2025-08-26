package com.DPhong.storeMe.security.filter;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.ErrorVO;
import com.DPhong.storeMe.enums.ErrorCode;
import com.DPhong.storeMe.service.authentication.BlacklistTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtBlacklistFilter extends OncePerRequestFilter {

  private final BlacklistTokenService blacklistTokenService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = request.getHeader("Authorization");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7);
      if (blacklistTokenService.isBlacklisted(accessToken)) {
        ErrorVO errorVO = new ErrorVO();
        errorVO.setErrorMessage("Token is revoked or expired.");
        errorVO.setErrorCode(ErrorCode.AUTH_FAILED.getCode());
        ApiResponse<Void> apiResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorVO);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
