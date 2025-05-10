package com.DPhong.storeMe.mapper;

import org.mapstruct.MappingTarget;

/**
 * GenericMapper is an interface that defines a contract for mapping between entities and DTOs.
 *
 * @param <E> the type of the entity
 * @param <I> the type of the input DTO
 * @param <O> the type of the output DTO
 */
public interface GenericMapper<E, I, O> {
  /**
   * Maps an entity to a DTO.
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  E requestToEntity(I request);

  /**
   * Maps a DTO to an entity.
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  O entityToResponse(E entity);

  /**
   * Maps a DTO to an entity and updates the existing entity.
   *
   * @param request the DTO to map
   * @param entity the existing entity to update
   */
  void partialUpdate(I request, @MappingTarget E entity);
}
