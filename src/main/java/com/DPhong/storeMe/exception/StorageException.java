package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import java.util.List;

public class StorageException extends ApiException {

  public StorageException(ErrorCode errorCode) {
    super(errorCode);
  }

  public StorageException(ErrorCode errorCode, String customMessage) {
    super(errorCode, customMessage);
  }

  public StorageException(ErrorCode errorCode, List<FieldError> fieldErrors) {
    super(errorCode, fieldErrors);
  }

  public StorageException(ErrorCode errorCode, String customMessage, List<FieldError> fieldErrors) {
    super(errorCode, customMessage, fieldErrors);
  }
}
