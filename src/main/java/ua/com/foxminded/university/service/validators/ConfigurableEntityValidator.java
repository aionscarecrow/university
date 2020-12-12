package ua.com.foxminded.university.service.validators;

import java.util.function.Predicate;

public interface ConfigurableEntityValidator<T> extends EntityValidator<T>{

	void addCreationRule(EntityRules<T> entityRule);

	void addDeletionRule(EntityRules<T> entityRule);

	void addUpdateRule(EntityRules<T> entityRule);

	void addAllOperationsRule(EntityRules<T> entityRule);

	void addIdRule(Predicate<Integer> rule, String description);

}
