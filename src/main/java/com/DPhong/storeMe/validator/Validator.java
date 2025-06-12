package com.DPhong.storeMe.validator;

import com.DPhong.storeMe.exception.BadRequestException;
import com.DPhong.storeMe.exception.DataConflictException;

/**
 * A generic interface for validating input of type I with entity E.
 *
 * @param <I> the type of input to validate
 */
public interface Validator<I, ID> {

  /**
   * Validates the input for creating a new entity.
   *
   * @param input the input to validate
   * @throws DataConflictException if there is a conflict with existing data
   * @throws BadRequestException if the input is malformed or invalid
   */
  public void validateCreate(I input);

  /**
   * Validates the input for updating an existing entity.
   *
   * @param input the input to validate
   * @param id the ID of the entity to update
   * @throws DataConflictException if there is a conflict with existing data
   * @throws BadRequestException if the input is malformed or invalid
   */
  public void validateUpdate(I input, ID id);
}
