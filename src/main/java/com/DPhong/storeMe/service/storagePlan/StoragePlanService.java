package com.DPhong.storeMe.service.storagePlan;

import com.DPhong.storeMe.dto.storagePlan.StoragePlanRequestDTO;
import com.DPhong.storeMe.dto.storagePlan.StoragePlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.service.CrudService;

public interface StoragePlanService
    extends CrudService<StoragePlan, Long, StoragePlanRequestDTO, StoragePlanResponseDTO> {
}
