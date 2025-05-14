package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.service.CrudService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * GenericController is a base controller class that provides CRUD operations for entities. The
 * class extends this class must have annotations @RestController and @RequestMapping
 *
 * @param<E> : entity
 * @param<I> : request inbound
 * @param<O> : response
 */
@RequiredArgsConstructor
public abstract class GenericController<E, I, O> {
  private final CrudService<E, Long, I, O> genericService;

  @Operation(
      summary = "Lấy danh sách các thông tin của thực thể",
      description =
          "Lấy danh sách các thực thể với phân trang và bộ lọc thông qua cú pháp truy vấn"
              + " turkraft/springfilter. \n"
              + " Về biểu thức truy vấn, bạn có thể xem qua tại đây:"
              + " https://github.com/turkraft/springfilter")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<O>>> getAll(
      @ParameterObject Pageable pageable, @Filter Specification<E> specification) {
    return ResponseEntity.ok(ApiResponse.success(genericService.findAll(specification, pageable)));
  }

  @Operation(
      summary = "Lấy thông tin thực thể theo id",
      description = "Lấy thông tin thực thể theo id. Nếu không tìm thấy sẽ trả về 404 Not Found")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(genericService.findById(id)));
  }

  @Operation(
      summary = "Tạo mới thực thể",
      description = "Tạo mới thực thể và trả về đối tượng đã tạo")
  @PostMapping
  public ResponseEntity<ApiResponse<O>> create(@Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(genericService.create(request)));
  }

  @Operation(
      summary = "Cập nhật thực thể theo id",
      description = "Cập nhật thực thể theo id. Nếu không tìm thấy sẽ trả về 404 Not Found")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> update(
      @PathVariable("id") Long id, @Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(genericService.update(id, request)));
  }

  @Operation(
      summary = "Xóa thực thể theo id",
      description = "Xóa thực thể theo id. Nếu không tìm thấy sẽ trả về 404 Not Found")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
    genericService.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(
      summary = "Xóa nhiều thực thể theo danh sách id",
      description =
          "Xóa nhiều thực thể theo danh sách id. Nếu không tìm thấy sẽ trả về 404 Not Found")
  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteAll(@RequestBody List<Long> ids) {
    genericService.deleteAllById(ids);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
