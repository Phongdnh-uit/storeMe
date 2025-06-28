package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import java.util.List;

public class AuthException extends ApiException {

  public AuthException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AuthException(ErrorCode errorCode, String customMessage) {
    super(errorCode, customMessage);
  }

  public AuthException(ErrorCode errorCode, List<FieldError> fieldErrors) {
    super(errorCode, fieldErrors);
  }

  public AuthException(ErrorCode errorCode, String customMessage, List<FieldError> fieldErrors) {
    super(errorCode, customMessage, fieldErrors);
  }
}
