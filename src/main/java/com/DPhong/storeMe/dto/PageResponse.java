package com.DPhong.storeMe.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Accessors(chain = true)
public class PageResponse<T> {
  private List<T> content;
  private int totalPages;
  private long totalElements;
  private int size;
  private int number;

  public static <T> PageResponse<T> from(Page<T> page) {
    return new PageResponse<T>()
        .setContent(page.getContent())
        .setTotalPages(page.getTotalPages())
        .setTotalElements(page.getTotalElements())
        .setSize(page.getSize())
        .setNumber(page.getNumber());
  }
}
