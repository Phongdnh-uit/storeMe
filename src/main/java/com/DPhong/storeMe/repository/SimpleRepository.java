package com.DPhong.storeMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * SimpleRepository interface that extends JpaRepository and JpaSpecificationExecutor. This
 * interface is used to define a repository for a specific entity type.
 *
 * @param <E> the entity type
 * @param <ID> the type of the entity's identifier
 */
@NoRepositoryBean
public interface SimpleRepository<E, ID>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {}
