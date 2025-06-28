package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final ErrorCode errorCode;
  private final List<FieldError> fieldErrors;

  /**
   * Constructor for BaseException with error code. * @param errorCode: the error code representing
   * the type of error * @return: a BaseException instance with the specified error code
   */
  public ApiException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = null;
  }

  /**
   * Constructor for BaseException with error code and field errors.
   *
   * @param errorCode: the error code representing the type of error
   * @param fieldErrors: a list of field errors that may provide more specific details about
   *     validation issues
   * @return: a BaseException instance with the specified error code and field errors
   */
  public ApiException(ErrorCode errorCode, List<FieldError> fieldErrors) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = fieldErrors;
  }

  /**
   * Constructor for BaseException with a custom message.
   *
   * @param errorCode: the error code representing the type of error
   * @param customMessage: a custom message to provide additional context for the error
   * @return: a BaseException instance with the specified error code and custom message
   */
  public ApiException(ErrorCode errorCode, String customMessage) {
    super(customMessage != null ? customMessage : errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = null;
  }

  /**
   * Constructor for BaseException with error code, custom message, and field errors.
   *
   * @param errorCode: the error code representing the type of error
   * @param customMessage: a custom message to provide additional context for the error
   * @param fieldErrors: a list of field errors that may provide more specific details about
   *     validation issues
   * @return: a BaseException instance with the specified error code, custom message, and field
   */
  public ApiException(ErrorCode errorCode, String customMessage, List<FieldError> fieldErrors) {
    super(customMessage != null ? customMessage : errorCode.getMessage());
    this.errorCode = errorCode;
    this.fieldErrors = fieldErrors;
  }
}
