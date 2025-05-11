package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Verification;
import com.DPhong.storeMe.enums.VerificationType;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends SimpleRepository<Verification, Long> {

  Optional<Verification> findByUserIdAndCodeAndType(
      Long userId, String code, VerificationType type);
}
