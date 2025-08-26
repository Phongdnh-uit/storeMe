package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authorization.RolePermission;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends SimpleRepository<RolePermission, Long> {}
