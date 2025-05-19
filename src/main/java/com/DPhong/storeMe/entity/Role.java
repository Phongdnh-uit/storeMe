package com.DPhong.storeMe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
  private List<User> users = new ArrayList<>();
}
