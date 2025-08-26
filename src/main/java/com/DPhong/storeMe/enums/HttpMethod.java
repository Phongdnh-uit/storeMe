package com.DPhong.storeMe.enums;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  DELETE,
  PATCH,
  HEAD,
  OPTIONS,
  TRACE;

  public static HttpMethod fromString(String method) {
    return HttpMethod.valueOf(method.toUpperCase());
  }
}
