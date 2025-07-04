package com.DPhong.storeMe.dto;

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
public class FieldError {
  private String key;
  private String message;

  public static FieldError from(String key, String message) {
    return FieldError.builder().key(key).message(message).build();
  }
}
