package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Role;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends SimpleRepository<Role, Long> {
  boolean existsByName(String name);

  Optional<Role> findByName(String name);
}
