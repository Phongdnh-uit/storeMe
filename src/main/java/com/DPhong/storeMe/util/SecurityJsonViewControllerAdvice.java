package com.DPhong.storeMe.util;

import com.DPhong.storeMe.constant.View;
import com.DPhong.storeMe.enums.RoleName;
import java.util.stream.Collectors;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

@RestControllerAdvice
public class SecurityJsonViewControllerAdvice extends AbstractMappingJacksonResponseBodyAdvice {

  @Override
  protected void beforeBodyWriteInternal(
      MappingJacksonValue bodyContainer,
      MediaType contentType,
      MethodParameter returnType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (SecurityContextHolder.getContext().getAuthentication() != null
        && SecurityContextHolder.getContext().getAuthentication().getAuthorities() != null) {
      var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
      var jsonViews =
          authorities.stream()
              .map(GrantedAuthority::getAuthority)
              .map(RoleName::fromString)
              .map(View.MAPPING::get)
              .collect(Collectors.toList());
      if (jsonViews.size() == 1) {
        bodyContainer.setSerializationView(jsonViews.get(0));
        return;
      }
      throw new IllegalArgumentException(
          "Ambiguous @JsonView declaration for roles "
              + authorities.stream()
                  .map(GrantedAuthority::getAuthority)
                  .collect(Collectors.joining(",")));
    }
  }
}
