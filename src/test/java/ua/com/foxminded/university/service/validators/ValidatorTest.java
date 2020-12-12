package ua.com.foxminded.university.service.validators;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.EntityValidationException;
import ua.com.foxminded.university.service.validators.ValidationRules.MemberRules;

@DisplayName("Entity Validator")
class ValidatorTest {
	
	EntityValidator<Member> validator;
	Member member;
	
	@Nested
	@DisplayName("Initially set rules")
	class ValidatorCreationTest {
		
		@BeforeEach
		void setUp() {
			validator = new MemberValidator<>();
			member = new Member();
		}
		
		
		@Test
		@DisplayName("throws if null validated for write operation")
		void testThrowsIfNullPassed() {
			assertThrows(
					EntityValidationException.class, 
					() -> validator.validateCreateable(null),
					"Should throw exception if null passed"
				);
			
			assertThrows(
					EntityValidationException.class, 
					() -> validator.validateDeletable(null),
					"Should throw exception if null passed"
				);
			
			assertThrows(
					EntityValidationException.class, 
					() -> validator.validateUpdatable(null),
					"Should throw exception if null passed"
				);
		}
		
		
		@Test
		@DisplayName("does not throw if entity validated for write operation")
		void testNotThrowsIfNonNullPassed() {
			member = new Member();
			assertAll(
					() -> validator.validateCreateable(member),
					() -> validator.validateDeletable(member),
					() -> validator.validateUpdatable(member)
				);
		}
		
		
		@ParameterizedTest
		@DisplayName("throws if 0 or lesser number validated as id")
		@ValueSource(ints = {0, -1, -65536, -4000000, -2147483648})
		void testThrowsIfZeroPassed(int invalidId) {
			assertThrows(
					EntityValidationException.class, 
					() -> validator.validateId(invalidId),
					"Should throw if " + invalidId + " passed"
				);
		}
		
		
		@ParameterizedTest
		@DisplayName("does not throw if positive number validated as id")
		@ValueSource(ints = {1, 100, 65536, 4000000, 2147483647})
		void testInitialIddRule(int validId) {
			assertAll(() -> validator.validateId(validId));
		}
	}
	
	
	@Nested
	@DisplayName("Adding id rule")
	class IdRuleTest {
		
		@BeforeEach
		void setUp() {
			validator = new MemberValidator<>();
			ConfigurableEntityValidator<Member> config =
					(ConfigurableEntityValidator<Member>) validator;
			config.addAllOperationsRule(MemberRules.HAS_ID);
			member = new Member();
		}
		
		
		@Test
		@DisplayName("throws if entity id not set")
		void testThrowsIfIdNotSet() {
			assertThrows(
					EntityValidationException.class,
					() -> validator.validateCreateable(member),
					"Should throw if entity id not set");
			assertThrows(
					EntityValidationException.class,
					() -> validator.validateDeletable(member),
					"Should throw if entity id not set");
			assertThrows(
					EntityValidationException.class,
					() -> validator.validateUpdatable(member),
					"Should throw if entity id not set");
		}
		
		
		@Test
		@DisplayName("does not throw if entity id set")
		void testNotThrowsIfIdSet() {
			member.setMemberId(1);
			assertAll(
					() -> validator.validateCreateable(member),
					() -> validator.validateDeletable(member),
					() -> validator.validateUpdatable(member)
				);
		}
	}
}


