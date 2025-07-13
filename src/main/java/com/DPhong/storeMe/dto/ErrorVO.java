package com.DPhong.storeMe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorVO {
  private String errorMessage;
  private Integer errorCode;
  private List<FieldError> fieldErrors;
}
