package com.DPhong.storeMe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
  private int statusCode;
  private String message;
  private T data;
  private ErrorVO error;

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), "success", data, null);
  }

  public static ApiResponse<Void> error(int code, ErrorVO error) {
    return new ApiResponse<>(code, "error", null, error);
  }
}
