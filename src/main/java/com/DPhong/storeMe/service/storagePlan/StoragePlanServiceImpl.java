package com.DPhong.storeMe.service.storagePlan;

import com.DPhong.storeMe.dto.storagePlan.StoragePlanRequestDTO;
import com.DPhong.storeMe.dto.storagePlan.StoragePlanResponseDTO;
import com.DPhong.storeMe.entity.StoragePlan;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.mapper.StoragePlanMapper;
import com.DPhong.storeMe.repository.StoragePlanRepository;
import com.DPhong.storeMe.service.GenericService;
import org.springframework.stereotype.Service;

@Service
public class StoragePlanServiceImpl
    extends GenericService<StoragePlan, StoragePlanRequestDTO, StoragePlanResponseDTO>
    implements StoragePlanService {

  public StoragePlanServiceImpl(StoragePlanRepository repository, StoragePlanMapper mapper) {
    super(repository, mapper, StoragePlan.class);
  }

  @Override
  protected void beforeCreateMapper(StoragePlanRequestDTO request) {
    validateStoragePlan(request);
  }

  @Override
  protected void beforeUpdateMapper(Long id, StoragePlanRequestDTO request, StoragePlan oldEntity) {
    validateStoragePlan(request);
  }

  private void validateStoragePlan(StoragePlanRequestDTO request) {
    if (((StoragePlanRepository) repository).existsByName(request.getName())) {
      throw new DataConflictException("Storage plan with this name already exists");
    }
  }
}
