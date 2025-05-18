package com.DPhong.storeMe.exception;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.ErrorDetail;
import com.DPhong.storeMe.dto.ErrorVO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
    ErrorVO errorVO = new ErrorVO();
    errorVO.setErrors(List.of(new ErrorDetail().setKey("error").setMessage(ex.getMessage())));
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Resource not found", errorVO));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
    ErrorVO errorVO = new ErrorVO();
    errorVO.setErrors(List.of(new ErrorDetail().setKey("error").setMessage(ex.getMessage())));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Bad request", errorVO));
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataConflict(DataConflictException ex) {
    ErrorVO errorVO = new ErrorVO();
    ex.getDetails()
        .forEach(
            (k, v) -> {
              errorVO.getErrors().add(new ErrorDetail().setKey(k).setMessage(v));
            });
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage(), errorVO));
  }

  @ExceptionHandler(VerificationException.class)
  public ResponseEntity<ApiResponse<Void>> handleVerification(VerificationException ex) {
    ErrorVO errorVO = new ErrorVO();
    errorVO.setErrors(List.of(new ErrorDetail().setKey("error").setMessage(ex.getMessage())));
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", errorVO));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
    ErrorVO errorVO = new ErrorVO();
    errorVO.setErrors(List.of(new ErrorDetail().setKey("error").setMessage(ex.getMessage())));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", errorVO));
  }
}
