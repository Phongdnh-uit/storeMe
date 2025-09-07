package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.Invoice;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends SimpleRepository<Invoice, Long> {}
