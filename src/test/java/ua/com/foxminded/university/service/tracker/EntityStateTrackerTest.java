package ua.com.foxminded.university.service.tracker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.service.trackers.EntityStateTracker;

@DisplayName("Entity state tracker")
class EntityStateTrackerTest {
	
	EntityStateTracker<Entity, Integer> tracker;
	Entity entity;
	
	@Nested
	@DisplayName("Track request")
	class TrackTest {
		
		@BeforeEach
		void setUp() throws Exception {
			tracker = new EntityStateTracker<>();
			tracker.setGetterFunctions(Entity::getId, Entity::getType);
		}
		
		@Test
		@DisplayName("accepts single entity")
		void testAcceptsSingle() {
			entity = new Entity(1,1);
			assertAll("Should accept entity", () -> tracker.track(entity));
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testsThrowsIfNull() {
			assertThrows(IllegalArgumentException.class, () -> tracker.track(null), 
					"Should throw IllegalArgumentException if null passed");
		}
		
		@Test
		@DisplayName("throws if id field null")
		void testThrowsIfIdNull() {
			entity = new Entity(null, 1);
			assertThrows(IllegalArgumentException.class, () -> tracker.track(entity),
					"Should throw IllegalArgumentException if id value is null");
		}
		
		@Test
		@DisplayName("throws if tracked field null")
		void testThrowsIfTrackedFieldNull() {
			entity = new Entity(1, null);
			assertThrows(IllegalArgumentException.class, () -> tracker.track(entity),
					"Should throw IllegalArgumentException if tracked field is null");
		}
	}
	
	@Nested
	@DisplayName("Track all")
	class TrackAllTest {
		
		@BeforeEach
		void setUp() throws Exception {
			tracker = new EntityStateTracker<>();
			tracker.setGetterFunctions(Entity::getId, Entity::getType);
		}
		
		@Test
		@DisplayName("accepts multiple entities")
		void testAcceptsMultiple() {
			entity = new Entity(1,1);
			List<Entity> entities = Collections.nCopies(3, entity);
			assertAll(
					"Should accept list of entities", 
					() -> tracker.trackAll(entities)
				);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testThrowsIfNull() {
			assertThrows(
					IllegalArgumentException.class,
					() -> tracker.trackAll(null),
					"Should throw if null argument passed");
		}
	}
	
	@Nested
	@DisplayName("Tracked field changed")
	class StateHasChangedTest {
		
		@BeforeEach
		void setUp() throws Exception {
			tracker = new EntityStateTracker<>();
			tracker.setGetterFunctions(Entity::getId, Entity::getType);
			
			entity = new Entity(1, 1);
		}

		@Test
		@DisplayName("returns false if tracked field not changed")
		void testFalseIfNotChanged() {
			tracker.track(entity);
			assertFalse(tracker.trackedFieldChanged(entity),
			"Should return false if tracked field not changed");
		}
		
		@Test
		@DisplayName("returns true if tracked field has changed")
		void testTrueIfChanged() {
			tracker.track(entity);
			entity.setType(2);
			assertTrue(tracker.trackedFieldChanged(entity));
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testThrowsIfNull() {
			assertThrows(
					IllegalArgumentException.class, 
					() -> tracker.trackedFieldChanged(null)
				);
		}
		
		@Test
		@DisplayName("throws if id field null")
		void testThrowsIfIdNull() {
			entity.setId(null);
			assertThrows(
					IllegalArgumentException.class, 
					() -> tracker.trackedFieldChanged(entity)
				);
		}
		
		@Test
		@DisplayName("throws if tracked field null")
		void testThrowsIfTrackedFieldNull() {
			entity.setType(null);
			assertThrows(
					IllegalArgumentException.class, 
					() -> tracker.trackedFieldChanged(entity)
				);
		}
	}
	
	@Nested
	@DisplayName("Entries eviction")
	class EvictionTest {
		
		private static final int TRACKER_CAPACITY = 2;
		
		@BeforeEach
		void setUp() throws Exception {
			tracker = new EntityStateTracker<>(TRACKER_CAPACITY);
			tracker.setGetterFunctions(Entity::getId, Entity::getType);
		}
		
		@Test
		@DisplayName("loses track of evicted entries")
		void testTrueForEvicted() {
			Entity entityToEvict = new Entity(1,1);
			Entity otherEntity = new Entity(2,2);
			Entity anotherEntity = new Entity(3,3);
			tracker.track(entityToEvict);
			tracker.track(otherEntity);
			tracker.track(anotherEntity);
			
			assertTrue(tracker.trackedFieldChanged(entityToEvict));
		}
		
		@Test
		@DisplayName("not affects tracking of kept entries")
		void testFalseForKept() {
			Entity entityToEvict = new Entity(1,1);
			Entity entityToKeep = new Entity(2,2);
			Entity otherEntity = new Entity(3,3);
			tracker.track(entityToEvict);
			tracker.track(entityToKeep);
			tracker.track(otherEntity);
			
			assertFalse(tracker.trackedFieldChanged(entityToKeep));
		}
		
	}
}

class Entity {
	
	private Integer id;
	private Integer type;
	
	public Entity(Integer id, Integer type) {
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(id, type);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		return Objects.equals(id, other.id) && Objects.equals(type, other.type);
	}
}
