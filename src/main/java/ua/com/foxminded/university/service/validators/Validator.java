package ua.com.foxminded.university.service.validators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.com.foxminded.university.service.exceptions.EntityValidationException;

public abstract class Validator<T> implements ConfigurableEntityValidator<T> {
	
	private Map<String, Predicate<T>> creationRules = new LinkedHashMap<>();
	private Map<String, Predicate<T>> updateRules = new LinkedHashMap<>();
	private Map<String, Predicate<T>> deletionRules = new LinkedHashMap<>();
	private Map<String, Predicate<Integer>> idRules = new LinkedHashMap<>();
	
	private RuleMatcher<T> writeRuleMatcher = new RuleMatcher<>();
	private RuleMatcher<Integer> idRuleMatcher = new RuleMatcher<>();
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public Validator() {
		addAllOperationsRule(() -> new Rule<>(Objects::nonNull, "is not null"));
		addIdRule(id -> id > 0, "id must be greater than zero");
		
		log.trace("{} initialized with default rule(s): {}", 
				this.getClass().getSimpleName(), this);
	}

	
	@Override
	public void validateCreateable(final T object) throws EntityValidationException {
		writeRuleMatcher.match(creationRules, object);
	}
	
	
	@Override
	public void validateUpdatable(final T object) throws EntityValidationException {
		writeRuleMatcher.match(updateRules, object);
	}
	
	
	@Override
	public void validateDeletable(final T object) throws EntityValidationException {
		writeRuleMatcher.match(deletionRules, object);
	}
	
	
	@Override
	public void validateId(final Integer id) throws EntityValidationException {
		idRuleMatcher.match(idRules, id);
	}

	
	@Override
	public void addCreationRule(EntityRules<T> entityRule) {
		log.debug("[{}] added as creation rule", 
				entityRule.getRule().getDescription());

		creationRules.put(entityRule.getRule().getDescription(), 
				entityRule.getRule().getDefinition());

	}
	
	
	@Override
	public void addDeletionRule(EntityRules<T> entityRule) {
		log.debug("[{}] added as deletion rule", 
				entityRule.getRule().getDescription());
		
		deletionRules.put(entityRule.getRule().getDescription(), 
				entityRule.getRule().getDefinition());

	}
	
	
	@Override
	public void addUpdateRule(EntityRules<T> entityRule) {
		log.debug("[{}] added as update rule", 
				entityRule.getRule().getDescription());
		
		updateRules.put(entityRule.getRule().getDescription(), 
				entityRule.getRule().getDefinition());

	}
	
	
	@Override
	public void addAllOperationsRule(EntityRules<T> entityRule) {
		log.info("Adding rule for all write operations");
		addCreationRule(entityRule);
		addUpdateRule(entityRule);
		addDeletionRule(entityRule);
	}
	
	
	@Override
	public void addIdRule(Predicate<Integer> rule, String description) {
		log.debug("[{}] added as id rule", description);
		idRules.put(description, rule);
	}
	
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder(getClass().getSimpleName());
		output.append(" [" + this.hashCode() + "]");
		
		output.append(":\nCreation rules: ");
		output.append(creationRules.keySet().toString());
		output.append("\nDeletion rules: ");
		output.append(deletionRules.keySet().toString());
		output.append("\nUpdate rules: ");
		output.append(updateRules.keySet().toString());
		output.append("\nId rules: ");
		output.append(idRules.keySet().toString());
		
		return output.toString();
	}
	
	
	private class RuleMatcher<S> {
		
		private void match(Map<String, Predicate<S>> rules, final S object) 
				throws EntityValidationException {
			for (Entry<String, Predicate<S>> ruleEntry : rules.entrySet()) {
				String rule = ruleEntry.getKey();
				if (log.isTraceEnabled()) {
					log.trace("Matching object: [{}] against rule: [{}]", object, rule);
				}
				if (!ruleEntry.getValue().test(object)) {
					log.error("Failed to match [{}] against rule [{}]", object, rule);
					throw new EntityValidationException("Validation failed against rule: " + rule);
				}
			}
		}
	}
	
}
