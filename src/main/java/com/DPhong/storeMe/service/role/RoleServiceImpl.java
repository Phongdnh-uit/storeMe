package com.DPhong.storeMe.service.role;

import com.DPhong.storeMe.dto.role.RoleRequestDTO;
import com.DPhong.storeMe.dto.role.RoleResponseDTO;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.entity.User;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.RoleMapper;
import com.DPhong.storeMe.repository.RoleRepository;
import com.DPhong.storeMe.repository.UserRepository;
import com.DPhong.storeMe.service.GenericService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends GenericService<Role, RoleRequestDTO, RoleResponseDTO>
    implements RoleService {

  private final UserRepository userRepository;

  public RoleServiceImpl(
      RoleRepository repository, RoleMapper mapper, UserRepository userRepository) {
    super(repository, mapper, Role.class);
    this.userRepository = userRepository;
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

  @Override
  public void assignRoleToUser(Long id, Long userId) {
    Role role =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    user.setRole(role);
    userRepository.save(user);
  }
}
