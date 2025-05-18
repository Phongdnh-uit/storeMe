package com.DPhong.storeMe.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorVO {
  private List<ErrorDetail> errors = new ArrayList<>();
}
