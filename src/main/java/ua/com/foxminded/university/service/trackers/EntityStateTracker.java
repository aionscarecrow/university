package ua.com.foxminded.university.service.trackers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityStateTracker<T, U> {
	
	private Function<T, U> idGetter;
	private Function<T, U> trackedFieldGetter;
	private Cache cache;
	
	private static final int CACHE_CAPACITY = 64;
	private static final Logger log = LoggerFactory.getLogger(EntityStateTracker.class);
	
	public EntityStateTracker() {
		this.cache = new Cache(CACHE_CAPACITY);
		log.debug("{} initialized with capacity of [{}]", 
				getClass().getSimpleName(),	CACHE_CAPACITY);
	}
	
	
	public EntityStateTracker(int cacheCapacity) {
		this.cache = new Cache(cacheCapacity);
		log.debug("{} initialized with capacity of [{}]", 
				getClass().getSimpleName(),	cacheCapacity);
	}
	
	
	public void setGetterFunctions(
			Function<T, U> idGetter, Function<T, U> trackedFieldGetter) {
		this.idGetter = idGetter;
		this.trackedFieldGetter = trackedFieldGetter;
	}
	
	
	public void track(T entity) {
		if(isNonTrackable(entity)) {
			log.error("Tracking request for [{}] failed", entity);
			throw new IllegalArgumentException(
					"Cannot track non-trackable entity");
		}
		
		cache.storeEntry(idGetter.apply(entity), trackedFieldGetter.apply(entity));
		log.trace("Entry added for entity [{}]", entity);
	}
	
	
	public void trackAll(List<T> entities) {
		if(areNonTrackable(entities)) {
			log.error("Received null as entity list");
			throw new IllegalArgumentException("Null entity list cannot be processed");
		}
		
		for(T entity : entities) {
			track(entity);
		}
	}
	
	
	public boolean trackedFieldChanged(T entity) {
		if(isNonTrackable(entity)) {
			log.error("State check request failed. Entity non-trackable [{}]", entity);
			throw new IllegalArgumentException("Cannot check state. Entity non-trackable");
		}
		
		U id = idGetter.apply(entity);
		U value = trackedFieldGetter.apply(entity);
		return !(Objects.equals(value, cache.getValue(id).orElse(null)));
	}
	
	
	private boolean isNonTrackable(T entity) {
		return(entity == null || idGetter.apply(entity) == null 
				|| trackedFieldGetter.apply(entity) == null);
	}
	
	
	private boolean areNonTrackable(List<T> entities) {
		return entities == null;
	}
	
	
	private final class Cache {
		
		private final Map<U, U> cachedData;
		private final int capacity;
		
		private final Logger log = LoggerFactory.getLogger(getClass());
		
		private Cache (int capacity) {
			this.cachedData = new LinkedHashMap<>(capacity, 0.75f, true);
			this.capacity = capacity;
		}
		
		private Optional<U> getValue(U key) {
			U value = cachedData.get(key);
			log.debug("Returning value [{}] for key [{}]", value, key);
			return Optional.ofNullable(value);
		}
		
		private void storeEntry(U key, U value) {
			cachedData.put(key, value);
			
			if(log.isDebugEnabled()) {
				log.debug("Stored entry [{}, {}]", key, value);
			}
			
			if(cachedData.size() > capacity) {
				log.trace("Removing least recently used entry");
				removeEldestEntry();
			}
		}
		
		private void removeEldestEntry() {
			cachedData.remove(getEldestKey());
		}
		
		private U getEldestKey() {
			return cachedData.keySet().iterator().next();
		}
		
	}


}
