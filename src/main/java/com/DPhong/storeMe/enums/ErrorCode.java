package com.DPhong.storeMe.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // ==== 1xxx - AUTH ====
  AUTH_FAILED(1001, "Xác thực thất bại", HttpStatus.UNAUTHORIZED.value()),
  TOKEN_EXPIRED(1002, "Token đã hết hạn", HttpStatus.UNAUTHORIZED.value()),
  INVALID_TOKEN(1003, "Token không hợp lệ", HttpStatus.UNAUTHORIZED.value()),
  ACCESS_DENIED(1004, "Truy cập bị từ chối", HttpStatus.FORBIDDEN.value()),
  LOGIN_PROVIDER_NOT_SUPPORTED(
      1005, "Nhà cung cấp đăng nhập không được hỗ trợ", HttpStatus.BAD_REQUEST.value()),
  INVALID_CREDENTIALS(1006, "Thông tin đăng nhập không hợp lệ", HttpStatus.BAD_REQUEST.value()),

  // ==== 2xxx - USER ====
  USER_NOT_FOUND(2001, "Người dùng không tồn tại", HttpStatus.NOT_FOUND.value()),
  USER_ALREADY_EXISTS(2002, "Người dùng đã tồn tại", HttpStatus.CONFLICT.value()),
  USER_DISABLED(2003, "Tài khoản bị vô hiệu hóa", HttpStatus.FORBIDDEN.value()),
  USER_UNVERIFIED(2004, "Tài khoản chưa xác minh", HttpStatus.FORBIDDEN.value()),

  // ==== 8xxx - BLOB STORAGE / FILE / FOLDER ====
  FILE_NOT_FOUND(8001, "Không tìm thấy tệp", HttpStatus.NOT_FOUND.value()),
  FILE_UPLOAD_FAILED(8002, "Tải tệp lên thất bại", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  FILE_DELETE_FAILED(8003, "Xóa tệp thất bại", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  FILE_TOO_LARGE(
      8004, "Kích thước tệp vượt quá giới hạn cho phép", HttpStatus.PAYLOAD_TOO_LARGE.value()),
  FILE_TYPE_NOT_SUPPORTED(
      8005, "Định dạng tệp không được hỗ trợ", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
  FILE_ACCESS_DENIED(8006, "Không có quyền truy cập tệp", HttpStatus.FORBIDDEN.value()),
  FILE_READ_FAILED(8007, "Đọc tệp thất bại", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  FILE_WRITE_FAILED(8008, "Ghi tệp thất bại", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  BLOB_SERVICE_UNAVAILABLE(
      8009, "Dịch vụ lưu trữ tạm thời không khả dụng", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  FILE_PATH_INVALID(8010, "Đường dẫn tệp không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  FILE_NAME_CONFLICT(8011, "Tệp cùng tên đã tồn tại", HttpStatus.CONFLICT.value()),
  CYCLIC_FILE_DETECTED(
      8012, "Phát hiện vòng lặp trong cấu trúc thư mục", HttpStatus.CONFLICT.value()),
  FSNODE_LOCKED(8013, "Tệp hoặc thư mục đang bị khóa", HttpStatus.CONFLICT.value()),

  // ==== 9xxx - SYSTEM / COMMON ====
  VALIDATION_FAILED(9001, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  INTERNAL_ERROR(9002, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  DATABASE_ERROR(9003, "Lỗi truy vấn cơ sở dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  RESOURCE_NOT_FOUND(9005, "Tài nguyên không tìm thấy", HttpStatus.NOT_FOUND.value()),
  RESOURCE_CONFLICT(9006, "Tài nguyên đã tồn tại", HttpStatus.CONFLICT.value()),
  DATA_INTEGRITY_VIOLATION(9007, "Vi phạm toàn vẹn dữ liệu", HttpStatus.CONFLICT.value()),
  UNEXPECTED_ERROR(9999, "Lỗi không mong muốn", HttpStatus.INTERNAL_SERVER_ERROR.value());

  private final Integer code;
  private final String message;
  private final Integer httpStatus;

  ErrorCode(Integer code, String message, Integer httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }

  public static final Optional<ErrorCode> fromCode(Integer code) {
    return Arrays.stream(ErrorCode.values())
        .filter(errorCode -> errorCode.getCode() == code)
        .findFirst();
  }
}
