package com.DPhong.storeMe.validator.folder;

import com.DPhong.storeMe.dto.folder.FolderRequestDTO;
import com.DPhong.storeMe.entity.Folder;
import com.DPhong.storeMe.enums.FolderType;
import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.DataConflictException;
import com.DPhong.storeMe.repository.FolderRepository;
import com.DPhong.storeMe.security.SecurityUtils;
import com.DPhong.storeMe.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FolderValidator implements Validator<FolderRequestDTO, Long> {

  private final SecurityUtils securityUtils;
  private final FolderRepository folderRepository;

  @Override
  public void validateCreate(FolderRequestDTO request) {

    Long currentUserId = securityUtils.getCurrentUserId();
    if (currentUserId == null) {
      throw new IllegalArgumentException("Current user ID cannot be null");
    }

    // Validate that the parent folder exists and belongs to the current user,
    // or use the user's root folder if no parent is specified
    Folder parentFolder =
        request.getParentId() != null && request.getParentId() > 0
            ? folderRepository
                .findById(request.getParentId())
                .filter(folder -> folder.getUser().getId().equals(currentUserId))
                .orElseThrow(() -> new BadRequestException("Parent folder not found"))
            : folderRepository
                .findByUserIdAndType(currentUserId, FolderType.USERROOT)
                .orElseThrow(() -> new IllegalArgumentException("User root folder not found"));

    // Check if the folder name already exists in the parent folder
    if (parentFolder.getSubFolders().stream()
        .anyMatch(f -> f.getName().equals(request.getName()))) {
      throw new DataConflictException(
          "Folder name already exists in the parent folder: " + request.getName());
    }
  }

  @Override
  public void validateUpdate(FolderRequestDTO input, Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'validateUpdate'");
  }
}
