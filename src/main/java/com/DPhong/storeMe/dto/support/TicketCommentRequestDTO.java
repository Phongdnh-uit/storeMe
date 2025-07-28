package com.DPhong.storeMe.dto.support;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCommentRequestDTO {
  @NotBlank
  @Max(500)
  private String content;
}
