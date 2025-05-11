package com.DPhong.storeMe.service;

import com.DPhong.storeMe.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * This interface defines the basic CRUD operations for a service.
 *
 * @param <E> the type of entity
 * @param <ID> the type of entity identifier
 * @param <I> the type of identifier
 * @param <O> the type of output
 */
public interface CrudService<E, ID, I, O> {

  /**
   * Retrieves all entities with pagination and filtering.
   *
   * @param specification the specification for filtering
   * @param pageable the pagination information
   * @return a page of entities
   */
  PageResponse<O> findAll(Specification<E> specification, Pageable pageable);

  /**
   * Retrieves an entity by its identifier.
   *
   * @param id the identifier of the entity
   * @return the retrieved entity
   */
  O findById(ID id);

  /**
   * Creates a new entity.
   *
   * @param entity the entity to create
   * @return the created entity
   */
  O create(I request);

  /**
   * Updates an existing entity.
   *
   * @param id the identifier of the entity to update
   * @param entity the updated entity
   * @return the updated entity
   */
  O update(ID id, I request);

  /**
   * Deletes an entity by its identifier.
   *
   * @param id the identifier of the entity to delete
   */
  void delete(ID id);

  /**
   * Deletes multiple entities by their identifiers.
   *
   * @param ids the identifiers of the entities to delete
   */
  void deleteAllById(Iterable<ID> ids);
}
