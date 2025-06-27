package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import java.util.List;

public class ResourceNotFoundException extends BaseException {

  public ResourceNotFoundException() {
    super(ErrorCode.RESOURCE_NOT_FOUND);
  }

  public ResourceNotFoundException(String customMessage) {
    super(ErrorCode.RESOURCE_NOT_FOUND, customMessage);
  }

  public ResourceNotFoundException(List<FieldError> fieldErrors) {
    super(ErrorCode.RESOURCE_NOT_FOUND, fieldErrors);
  }

  public ResourceNotFoundException(String customMessage, List<FieldError> fieldErrors) {
    super(ErrorCode.RESOURCE_NOT_FOUND, customMessage, fieldErrors);
  }
}
