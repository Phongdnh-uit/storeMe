package com.DPhong.storeMe.service;

import com.DPhong.storeMe.dto.PageResponse;
import com.DPhong.storeMe.exception.ResourceNotFoundException;
import com.DPhong.storeMe.mapper.GenericMapper;
import com.DPhong.storeMe.repository.SimpleRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Generic service implementation for CRUD operations.
 *
 * @param <E> the entity type
 * @param <I> the request type
 * @param <O> the response type
 */
@RequiredArgsConstructor
public abstract class GenericService<E, I, O> implements CrudService<E, Long, I, O> {
  protected final SimpleRepository<E, Long> repository;
  protected final GenericMapper<E, I, O> mapper;
  protected final Class<E> entityClass;

  /**
   * @param id the id of the entity to find
   * @return the entity with the given id
   * @throws ResourceNotFoundException if the entity is not found
   */
  protected E findByIdOrThrow(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException(entityClass.getSimpleName() + " not found"));
  }

  @Override
  public PageResponse<O> findAll(Specification<E> specification, Pageable pageable) {
    Page<E> page = repository.findAll(specification, pageable);
    return new PageResponse<O>()
        .setContent(page.getContent().stream().map(mapper::entityToResponse).toList())
        .setNumber(page.getNumber())
        .setSize(page.getSize())
        .setTotalElements(page.getTotalElements())
        .setTotalPages(page.getTotalPages());
  }

  @Override
  public O findById(Long id) {
    return mapper.entityToResponse(findByIdOrThrow(id));
  }

  // ====================================CREATE==================================
  protected void beforeCreateMapper(I request) {}

  @Override
  public O create(I request) {
    beforeCreateMapper(request);
    E entity = mapper.requestToEntity(request);
    afterCreateMapper(request, entity);
    E savedEntity = repository.save(entity);
    return mapper.entityToResponse(savedEntity);
  }

  protected void afterCreateMapper(I request, E entity) {}

  // =====================================UPDATE================================
  /** This function will be call berore the update mapper. after check the entity is exists. */
  protected void beforeUpdateMapper(Long id, I request) {}

  @Override
  public O update(Long id, I request) {
    E entity = findByIdOrThrow(id);
    beforeUpdateMapper(id, request);
    mapper.partialUpdate(request, entity);
    afterUpdateMapper(id, request, entity);
    entity = repository.save(entity);
    return mapper.entityToResponse(entity);
  }

  protected void afterUpdateMapper(Long id, I request, E entity) {}

  // =======================================DELETE================================
  @Override
  public void delete(Long id) {
    E entity = findByIdOrThrow(id);
    repository.delete(entity);
  }

  // ========================================DELETE-ALL===========================
  @Override
  public void deleteAllById(Iterable<Long> ids) {
    List<E> entities = repository.findAllById(ids);
    if (entities.size() != StreamSupport.stream(ids.spliterator(), false).count()) {
      throw new ResourceNotFoundException("Some " + entityClass.getSimpleName() + " not found");
    }
    repository.deleteAll(entities);
  }
}
