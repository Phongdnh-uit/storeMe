package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.service.GenericService;
import com.turkraft.springfilter.boot.Filter;
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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public abstract class GenericController<E, I, O> {
  private final GenericService<E, I, O> genericService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<O>>> getAll(
      @ParameterObject Pageable pageable, @Filter Specification<E> specification) {
    return ResponseEntity.ok(ApiResponse.success(genericService.findAll(specification, pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(genericService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<O>> create(@Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(genericService.create(request)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> update(
      @PathVariable("id") Long id, @Valid @RequestBody I request) {
    return ResponseEntity.ok(ApiResponse.success(genericService.update(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
    genericService.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteAll(@RequestBody List<Long> ids) {
    genericService.deleteAllById(ids);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
