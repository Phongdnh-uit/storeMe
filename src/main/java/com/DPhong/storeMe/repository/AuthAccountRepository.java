package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.AuthAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthAccountRepository extends SimpleRepository<AuthAccount, Long> {}
