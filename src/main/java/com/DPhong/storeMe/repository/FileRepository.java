package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.File;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends SimpleRepository<File, Long> {}
