package com.DPhong.storeMe.repository;

import com.DPhong.storeMe.entity.tag.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends SimpleRepository<Tag, Long> {}
