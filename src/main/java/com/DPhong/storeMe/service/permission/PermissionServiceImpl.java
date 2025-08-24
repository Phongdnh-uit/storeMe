package com.DPhong.storeMe.service.permission;

import com.DPhong.storeMe.dto.permission.PermissionRequestDTO;
import com.DPhong.storeMe.dto.permission.PermissionResponseDTO;
import com.DPhong.storeMe.entity.Permission;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.mapper.PermissionMapper;
import com.DPhong.storeMe.repository.PermissionRepository;
import com.DPhong.storeMe.service.GenericService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl
    extends GenericService<Permission, PermissionRequestDTO, PermissionResponseDTO>
    implements PermissionService {

  public PermissionServiceImpl(PermissionRepository repository, PermissionMapper mapper) {
    super(repository, mapper);
  }

  @Override
  protected void beforeCreateMapper(PermissionRequestDTO request) {
    validateRequest(request, null);
  }

  @Override
  protected void beforeUpdateMapper(Long id, PermissionRequestDTO request, Permission oldEntity) {
    validateRequest(request, oldEntity.getId());
  }

  // ============================ HELPER METHOD ============================
  void validateRequest(PermissionRequestDTO request, Long id) {
    Specification<Permission> checkSpec =
        (root, _, builder) ->
            builder.and(
                builder.equal(root.get("urlPattern"), request.getUrlPattern()),
                builder.equal(root.get("method"), request.getMethod()));
    if (id != null) {
      checkSpec = checkSpec.and((root, _, builder) -> builder.notEqual(root.get("id"), id));
    }
    if (repository.exists(checkSpec)) {
      throw new DataConflictException(
          "Permission with the same URL pattern and method already exists.");
    }
  }
}
