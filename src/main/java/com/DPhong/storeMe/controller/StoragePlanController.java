package com.DPhong.storeMe.controller;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.dto.storagePlan.StoragePlanRequestDTO;
import com.DPhong.storeMe.dto.storagePlan.StoragePlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.service.storagePlan.StoragePlanService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Storage Plan", description = "Storage Plan API")
@RequestMapping(AppConstant.BASE_URL + "/storage-plans")
@RestController
public class StoragePlanController
    extends GenericController<StoragePlan, StoragePlanRequestDTO, StoragePlanResponseDTO> {

  public StoragePlanController(StoragePlanService service) {
    super(service);
  }
}
