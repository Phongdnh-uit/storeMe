package com.DPhong.storeMe.service.role;

import com.DPhong.storeMe.dto.role.RoleRequestDTO;
import com.DPhong.storeMe.dto.role.RoleResponseDTO;
import com.DPhong.storeMe.entity.Role;
import com.DPhong.storeMe.service.CrudService;

public interface RoleService extends CrudService<Role, Long, RoleRequestDTO, RoleResponseDTO> {}
