package com.DPhong.storeMe.service.role;

import com.DPhong.storeMe.dto.role.RoleRequestDTO;
import com.DPhong.storeMe.dto.role.RoleResponseDTO;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.mapper.RoleMapper;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.service.GenericService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends GenericService<Role, RoleRequestDTO, RoleResponseDTO>
    implements RoleService {

  public RoleServiceImpl(RoleRepository repository, RoleMapper mapper) {
    super(repository, mapper, Role.class);
  }

  @Override
  protected void beforeCreateMapper(RoleRequestDTO request) {
    if (((RoleRepository) repository).existsByName(request.getName())) {
      throw new DataConflictException("Role already exists", Map.of("name", request.getName()));
    }
  }

  @Override
  protected void beforeUpdateMapper(Long id, RoleRequestDTO request, Role oldEntity) {
    if (!oldEntity.getName().equals(request.getName())
        && ((RoleRepository) repository).existsByName(request.getName())) {
      throw new DataConflictException("Role already exists", Map.of("name", request.getName()));
    }
  }
}
