package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.authentication.Verification;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends SimpleRepository<Verification, Long> {}
