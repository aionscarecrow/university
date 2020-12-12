package ua.com.foxminded.university.service.validators;

import ua.com.foxminded.university.service.exceptions.EntityValidationException;

public interface EntityValidator<T> {

	void validateCreateable(T object) throws EntityValidationException;

	void validateUpdatable(T object) throws EntityValidationException;

	void validateDeletable(T object) throws EntityValidationException;

	void validateId(Integer id) throws EntityValidationException;


}