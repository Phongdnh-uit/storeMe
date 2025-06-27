package com.DPhong.storeMe.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum ErrorCode {
  // ==== 1xxx - AUTH ====
  AUTH_FAILED("AUTH_FAILED", 1001, "Đăng nhập thất bại"),
  TOKEN_EXPIRED("TOKEN_EXPIRED", 1002, "Token đã hết hạn"),
  INVALID_TOKEN("INVALID_TOKEN", 1003, "Token không hợp lệ"),
  ACCESS_DENIED("ACCESS_DENIED", 1004, "Truy cập bị từ chối"),

  // ==== 2xxx - USER ====
  USER_NOT_FOUND("USER_NOT_FOUND", 2001, "Người dùng không tồn tại"),
  USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", 2002, "Người dùng đã tồn tại"),
  USER_DISABLED("USER_DISABLED", 2003, "Tài khoản bị vô hiệu hóa"),
  USER_UNVERIFIED("USER_UNVERIFIED", 2004, "Tài khoản chưa xác minh"),

  // ==== 8xxx - BLOB STORAGE / FILE ====
  FILE_NOT_FOUND("FILE_NOT_FOUND", 8001, "Không tìm thấy tệp"),
  FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", 8002, "Tải tệp lên thất bại"),
  FILE_DELETE_FAILED("FILE_DELETE_FAILED", 8003, "Xóa tệp thất bại"),
  FILE_TOO_LARGE("FILE_TOO_LARGE", 8004, "Kích thước tệp vượt quá giới hạn cho phép"),
  FILE_TYPE_NOT_SUPPORTED("FILE_TYPE_NOT_SUPPORTED", 8005, "Định dạng tệp không được hỗ trợ"),
  FILE_ACCESS_DENIED("FILE_ACCESS_DENIED", 8006, "Không có quyền truy cập tệp"),
  FILE_READ_FAILED("FILE_READ_FAILED", 8007, "Đọc tệp thất bại"),
  FILE_WRITE_FAILED("FILE_WRITE_FAILED", 8008, "Ghi tệp thất bại"),
  BLOB_SERVICE_UNAVAILABLE(
      "BLOB_SERVICE_UNAVAILABLE", 8009, "Dịch vụ lưu trữ tạm thời không khả dụng"),
  FILE_PATH_INVALID("FILE_PATH_INVALID", 8010, "Đường dẫn tệp không hợp lệ"),
  FILE_NAME_CONFLICT("FILE_NAME_CONFLICT", 8011, "Tệp cùng tên đã tồn tại"),

  // ==== 9xxx - SYSTEM / COMMON ====
  VALIDATION_FAILED("VALIDATION_FAILED", 9001, "Dữ liệu không hợp lệ"),
  INTERNAL_ERROR("INTERNAL_ERROR", 9002, "Lỗi hệ thống"),
  DATABASE_ERROR("DATABASE_ERROR", 9003, "Lỗi truy vấn cơ sở dữ liệu"),
  SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", 9004, "Dịch vụ tạm thời không khả dụng"),
  RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", 9005, "Tài nguyên không tìm thấy"),
  RESOURCE_CONFLICT("RESOURCE_CONFLICT", 9006, "Tài nguyên đã tồn tại");

  private final String code;
  private final int codeInt;
  private final String message;

  ErrorCode(String code, int codeInt, String message) {
    this.code = code;
    this.codeInt = codeInt;
    this.message = message;
  }

  public static final Optional<ErrorCode> fromCode(String code) {
    return Arrays.stream(ErrorCode.values())
        .filter(errorCode -> errorCode.getCode().equals(code))
        .findFirst();
  }
}
