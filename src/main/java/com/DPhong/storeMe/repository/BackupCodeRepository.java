package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authentication.BackupCode;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupCodeRepository extends SimpleRepository<BackupCode, Long> {}
