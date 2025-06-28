package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.FileMetadata;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends SimpleRepository<FileMetadata, Long> {}
