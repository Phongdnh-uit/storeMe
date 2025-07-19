package com.DPhong.storeMe.dto.sharing;

import com.DPhong.storeMe.enums.SharingType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSharingRequestDTO {

  @NotNull private Long grantedTo;

  @NotEmpty private List<SharingType> permissions;
}
