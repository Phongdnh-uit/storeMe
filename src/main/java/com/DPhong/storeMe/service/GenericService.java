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
    return repository
        .findById(id)
        .map(mapper::entityToResponse)
        .orElseThrow(() -> new ResourceNotFoundException(entityClass.getName() + " not found"));
  }

  @Override
  public O create(I request) {
    E entity = mapper.requestToEntity(request);
    E savedEntity = repository.save(entity);
    return mapper.entityToResponse(savedEntity);
  }

  @Override
  public O update(Long id, I request) {
    E entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(entityClass.getName() + " not found"));
    mapper.partialUpdate(request, entity);
    entity = repository.save(entity);
    return mapper.entityToResponse(entity);
  }

  @Override
  public void delete(Long id) {
    E entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(entityClass.getName() + " not found"));
    repository.delete(entity);
  }

  @Override
  public void deleteAllById(Iterable<Long> ids) {
    List<E> entities = repository.findAllById(ids);
    if (entities.size() != StreamSupport.stream(ids.spliterator(), false).count()) {
      throw new ResourceNotFoundException("Some " + entityClass.getName() + " not found");
    }
    repository.deleteAll(entities);
  }
}
