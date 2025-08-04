package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.ErrorVO;
import com.DPhong.storeMe.dto.FieldError;
import com.DPhong.storeMe.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ex.getErrorCode().getCode())
            .errorMessage(ex.getErrorCode().getMessage())
            .fieldErrors(ex.getFieldErrors())
            .build();
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), errorVO));
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataConflict(DataConflictException ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ex.getErrorCode().getCode())
            .errorMessage(ex.getErrorCode().getMessage())
            .fieldErrors(ex.getFieldErrors())
            .build();
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(ApiResponse.error(ex.getErrorCode().getHttpStatus(), errorVO));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
            .errorMessage("Validation failed for one or more fields.")
            .fieldErrors(
                ex.getBindingResult().getFieldErrors().stream()
                    .map(
                        fieldError ->
                            FieldError.from(fieldError.getField(), fieldError.getDefaultMessage()))
                    .toList())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorVO));
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ex.getErrorCode().getCode())
            .errorMessage(
                ex.getMessage() != null ? ex.getMessage() : ex.getErrorCode().getMessage())
            .fieldErrors(ex.getFieldErrors())
            .build();
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(ApiResponse.error(ex.getErrorCode().getHttpStatus(), errorVO));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
      BadCredentialsException ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ErrorCode.AUTH_FAILED.getCode())
            .errorMessage("Invalid email or password.")
            .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorVO));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
    ErrorVO errorVO =
        ErrorVO.builder()
            .errorCode(ErrorCode.UNEXPECTED_ERROR.getCode())
            .errorMessage("An unexpected error occurred.")
            .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorVO));
  }
}
