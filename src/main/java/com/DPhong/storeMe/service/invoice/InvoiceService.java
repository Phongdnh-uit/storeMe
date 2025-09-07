package com.DPhong.storeMe.service.invoice;

import com.DPhong.storeMe.dto.invoice.InvoiceResponseDTO;
import com.DPhong.storeMe.dto.userPlan.SubscribeRequestDTO;

public interface InvoiceService {
  InvoiceResponseDTO createInvoice(SubscribeRequestDTO subscribeRequestDTO);
}
