package com.DPhong.storeMe.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PageResponse<T> {
  private List<T> content;
  private int totalPages;
  private long totalElements;
  private int size;
  private int number;
}
