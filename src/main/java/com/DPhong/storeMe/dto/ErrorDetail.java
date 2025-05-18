package com.DPhong.storeMe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ErrorDetail {
  private String key;
  private String message;
}
