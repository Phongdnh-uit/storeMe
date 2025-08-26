package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authentication.User2FAMethod;
import org.springframework.stereotype.Repository;

@Repository
public interface User2FAMethodRepository extends SimpleRepository<User2FAMethod, Long> {}
