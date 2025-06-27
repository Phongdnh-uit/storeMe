package com.DPhong.storeMe.entity;

import com.DPhong.storeMe.enums.FSType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fs_nodes")
public class FSNode extends BaseEntity {
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

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private FSNode parent;

  @OneToOne(mappedBy = "file")
  private FileMetadata fileMetadata;

  @Enumerated(EnumType.STRING)
  private FSType type;

  private boolean isHidden = false;

  private boolean isLocked = false;

  private Instant lastAccessed;

  private Instant deletedAt;
}
