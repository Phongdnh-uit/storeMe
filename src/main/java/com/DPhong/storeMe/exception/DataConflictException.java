package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class DataConflictException extends BaseException {

  public DataConflictException() {
    super(ErrorCode.RESOURCE_CONFLICT);
  }

  public DataConflictException(String message) {
    super(ErrorCode.RESOURCE_CONFLICT, message);
  }

  public DataConflictException(String customMessage, List<FieldError> fieldErrors) {
    super(ErrorCode.RESOURCE_CONFLICT, customMessage);
  }

  public DataConflictException(List<FieldError> fieldErrors) {
    super(ErrorCode.RESOURCE_CONFLICT, fieldErrors);
  }
}
