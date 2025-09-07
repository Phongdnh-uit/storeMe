package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.dto.ApiResponse;
import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.dto.invoice.InvoiceResponseDTO;
import com.DPhong.storeMe.dto.userPlan.SubscribeRequestDTO;
import com.DPhong.storeMe.dto.userPlan.UserPlanResponseDTO;
import com.DPhong.storeMe.entity.plan.UserPlan;
import com.DPhong.storeMe.service.invoice.InvoiceService;
import com.DPhong.storeMe.service.userPlan.UserPlanService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Plan", description = "Đăng kí gói dịch vụ")
@RequiredArgsConstructor
@RequestMapping("/user-plans")
@RestController
public class UserPlanController {
  private final UserPlanService userPlanService;
  private final InvoiceService invoiceService;

  @Operation(
      summary = "Đăng kí gói dịch vụ",
      description =
          "Đăng kí gói dịch vụ cho người dùng. Chỉ có thể đăng kí 1 gói dịch vụ tại 1 thời điểm."
              + " Nếu người dùng đã có gói dịch vụ, gói dịch vụ cũ sẽ bị hủy bỏ và gói dịch vụ mới"
              + " sẽ được đăng kí. Tiền của gói dịch vụ cũ sẽ được tính lại và trừ vào gói dịch vụ"
              + " mới.")
  @PostMapping("/subscribe")
  public ResponseEntity<ApiResponse<InvoiceResponseDTO>> subscribeToPlan(
      @Valid @RequestBody SubscribeRequestDTO request) {
    return ResponseEntity.ok(ApiResponse.success(invoiceService.createInvoice(request)));
  }

  @Operation(
      summary = "Lấy thông tin gói dịch vụ hiện tại",
      description = "Lấy thông tin gói dịch vụ hiện tại của người dùng.")
  @GetMapping("/current")
  public ResponseEntity<ApiResponse<UserPlanResponseDTO>> getCurrentUserPlan() {
    return ResponseEntity.ok(ApiResponse.success(userPlanService.getCurrentPlan()));
  }

  @Operation(
      summary = "Lấy lịch sử gói dịch vụ",
      description =
          "Lấy lịch sử gói dịch vụ của người dùng. Chỉ có thể lấy lịch sử gói dịch vụ của người"
              + " dùng hiện tại.")
  @GetMapping("/history")
  public ResponseEntity<ApiResponse<PageResponse<UserPlanResponseDTO>>> getUserPlanHistory(
      @ParameterObject Pageable pageable,
      @Parameter(
              name = "filter",
              description = "Bộ lọc thông qua cú pháp truy vấn turkraft/springfilter",
              example = "name~'abc'",
              required = false)
          @Filter
          Specification<UserPlan> specification) {
    return ResponseEntity.ok(
        ApiResponse.success(userPlanService.getUserPlanHistory(specification, pageable)));
  }

  @Operation(
      summary = "Hủy gói dịch vụ hiện tại",
      description =
          "Hủy gói dịch vụ hiện tại của người dùng. Nếu người dùng không có gói dịch vụ nào, sẽ"
              + " không có gì xảy ra.")
  @DeleteMapping("/unsubscribe")
  public ResponseEntity<ApiResponse<Void>> unsubscribeCurrentPlan() {
    userPlanService.cancelCurrentPlan();
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
