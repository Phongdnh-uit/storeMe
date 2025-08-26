package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authorization.Permission;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends SimpleRepository<Permission, Long> {}
