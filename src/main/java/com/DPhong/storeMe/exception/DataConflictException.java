package com.DPhong.storeMe.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class DataConflictException extends RuntimeException {

  private final Map<String, String> details = new HashMap<>();

  public DataConflictException(String message) {
    super(message);
  }

  public DataConflictException(String message, Map<String, String> details) {
    super(message);
    this.details.putAll(details);
  }
}
