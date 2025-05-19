# Store Me - Hệ thống lưu trữ file

Hệ thống lưu trữ file là một ứng dụng đơn giản giúp người dùng tải lên, quản lý và tải xuống các tệp tin một cách an toàn và hiệu quả. Dự án này được thiết kế để cung cấp một giải pháp lưu trữ file cơ bản, dễ sử dụng và có thể mở rộng.

## Tính năng

- Tải lên file: Hỗ trợ tải lên các loại tệp tin phổ biến (PDF, hình ảnh, tài liệu, v.v.).
- Quản lý file: Xem danh sách, tìm kiếm và xóa các tệp tin đã tải lên.
- Tải xuống file: Cho phép người dùng tải file về máy tính.
- Bảo mật: Đảm bảo file được lưu trữ an toàn với kiểm tra quyền truy cập.

## Yêu cầu

- JDK: 24
- GraalVM

## Cài đặt

1. Tải mã nguồn

```
  git clone https://github.com/Phongdnh-uit/storeMe.git
  cd storeMe
```

2. Chạy docker

```
docker compose up -d
```

3. Cấu hình nơi lưu trữ file

```
storage:
  root:
    location: <path-to-store-file>
```

4. Thêm phụ thuộc vào application.yml

```
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: <username>@gmail.com
    password: <app-password>
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.ssl.trust: smtp.gmail.com
```

5. Chạy dự án

```
./gradlew build
./gradlew bootRun
```

## Cấu trúc thư mục

```
storeMe/
├── config/
├── constant/
├── controller/
├── dto/
├── entity/
├── enums/
├── exception/
├── mapper/
├── repository/
├── security/
├── service/
resources
├── db/
├── static
├── templates
└── application.yml
```
