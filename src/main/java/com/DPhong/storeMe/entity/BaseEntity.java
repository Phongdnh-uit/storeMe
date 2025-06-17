package com.DPhong.storeMe.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Accessors(chain = true)
@MappedSuperclass
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @Column(nullable = false, updatable = false)
  @CreatedDate
  private Instant createdAt;

  @Column(nullable = false)
  @LastModifiedDate
  private Instant updatedAt;

  @Column(nullable = false, updatable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @LastModifiedBy
  private Long updatedBy;
}
