package com.DPhong.storeMe.exception;

public class TikaAnalysisException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public TikaAnalysisException(String message) {
    super(message);
  }

  public TikaAnalysisException(String message, Throwable cause) {
    super(message, cause);
  }
}
