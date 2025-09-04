package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.service.CrudService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * class extends this class must have annotations @RestController and @RequestMapping and @Tag
 *
 * @param<E> : entity
 * @param<I> : request inbound
 * @param<O> : response
 */
@RequiredArgsConstructor
public abstract class GenericController<E, I, O> {
  protected final CrudService<E, Long, I, O> service;

  @Operation(
      operationId = "getAll{Entity}",
      summary = "Lấy danh sách {Entity}",
      description = "pagination + filter với turkraft/springfilter")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<O>>> getAll(
      @ParameterObject Pageable pageable,
      @Parameter(
              name = "filter",
              description =
                  "Filter by field with turkraft/springfilter. Example:"
                      + " filter=field1=='value1' and field2>5",
              schema = @Schema(type = "string"),
              required = false)
          @Filter
          Specification<E> specification) {
    pageable = pageable.isPaged() ? pageable : Pageable.unpaged();
    return ResponseEntity.ok(ApiResponse.success(service.findAll(specification, pageable)));
  }

  @Operation(operationId = "get{Entity}ById", summary = "Lấy {Entity} theo id")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
  }

  @Operation(operationId = "create{Entity}", summary = "Tạo mới {Entity}")
  @PostMapping
  public ResponseEntity<ApiResponse<O>> create(@Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(service.create(request)));
  }

  @Operation(operationId = "update{Entity}ById", summary = "Cập nhật {Entity} theo id")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> update(
      @PathVariable("id") Long id, @Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(service.update(id, request)));
  }

  @Operation(operationId = "delete{Entity}ById", summary = "Xóa {Entity} theo id")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
    service.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(operationId = "delete{Entity}Bulk", summary = "Xóa nhiều {Entity} theo danh sách id")
  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteAll(@RequestBody List<Long> ids) {
    service.deleteAllById(ids);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
