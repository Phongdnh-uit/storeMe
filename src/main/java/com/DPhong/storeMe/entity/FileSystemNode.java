package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class FileSystemNode extends BaseEntity {
  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long size = 0L;

  // ancestor not containing the current node
  @Column(columnDefinition = "BIGINT[]")
  private List<Long> ancestor = new ArrayList<>();

  private Instant deletedAt;

  public abstract boolean isDirectory();
}
