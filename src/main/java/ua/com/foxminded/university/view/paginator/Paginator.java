package ua.com.foxminded.university.view.paginator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paginator<T> {
	
	private final Cache<T> cache;
	private int itemsPerPage = 5;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	public Paginator() {
		this.cache = new Cache<>();
		log.debug("Created default instance: elements per page [{}]",	itemsPerPage);
	}
	
	public Paginator(int cacheTTL, int ... itemsPerPage) {
		this.cache = new Cache<>(cacheTTL);
		if (itemsPerPage.length != 0 && itemsPerPage[0] > 0) {
			this.itemsPerPage = itemsPerPage[0];
		}
		
	}
	

	public PageData<T> getPageData(final int pageNumber, Class<?>...classes) {
		
		if (cache.isEmpty(classes)) {
			log.debug("Cache empty. Returning empty PageData");
			return new PageData<>();
		}
		
		log.debug("Requested page number [{}]", pageNumber);
		
		int validPageNumber = getNearestValidPage(pageNumber, classes);

		log.debug("Serving page number [{}]", validPageNumber);


		int lastIndex = (cache.size(classes) - 1);
		int fromIndex = (validPageNumber - 1) * itemsPerPage;
		int toIndex = Math.min(fromIndex + itemsPerPage - 1, lastIndex);
		
		PageData<T> pageData = new PageData<>();
		pageData.setPageCount(countPages(classes));
		pageData.setPageNumber(validPageNumber);
		pageData.setPageContent(cache.subList(fromIndex, toIndex + 1, classes));
		
		log.trace("PageData instance created with [{}] elements", 
				pageData.getPageSize());
		
		return pageData;
	}
	

	private int getNearestValidPage(final int requestedPage, Class<?>...classes) {
		int totalPages = countPages(classes);
		log.trace("Validating page request [{}/{}]", requestedPage, totalPages);
		boolean outOfPages = (requestedPage > totalPages);
		
		int validPage = requestedPage;
		
		if (requestedPage < 1) {
			validPage = 1;
		} else if (outOfPages) {
			validPage = totalPages;
		}
		
		log.trace("Returning nearest valid page number [{}/{}]", 
				validPage, totalPages);
		return validPage;
	}


	private int countPages(Class<?>...classes) {
		int cacheSize = cache.size(classes);
		int fullPages = cacheSize / itemsPerPage; 
		int tailIndices = cacheSize - fullPages * itemsPerPage;
		return fullPages + ((tailIndices > 0)? 1 : 0);
	}
	

	public boolean hasValidCache() {
		return this.cache.isValid();
	}
	

	public void invalidateCache() {
		this.cache.invalidate();
	}
	

	public int getItemsPerPage() {
		return itemsPerPage;
	}
	

	public void setData(List<T> data, Class<?>... classes) {
		if(data == null) {
			throw new IllegalArgumentException("Pageable data can't be set to null");
		}
		log.debug("Received [{}] entries, [{}]", data.size(), Arrays.deepToString(classes));
		cache.setData(data, classes);
	}

	
	protected List<T> getData(Class<?>... classes){
		return cache.getData(classes);
	}


	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	
	
	private class Cache<S extends T> {

		private List<S> bulkData;
		private Map<Class<?>, List<Integer>> classIndices;
		private long expirationTimestamp = 0L;
		private int cacheTTL = 60;
		
		private Cache() {
			this.bulkData = new ArrayList<>();
			this.classIndices = new HashMap<>();
			log.debug(
					"Created default instance: expirationInterval [{}], expirationTimestamp [{}]", 
					this.cacheTTL, expirationTimestamp
					);
		}

		private Cache(int cacheTTL) {
			this.bulkData = new ArrayList<>();
			this.classIndices = new HashMap<>();
			this.cacheTTL = cacheTTL;
			log.debug(
					"Created instance: expirationInterval [{}], expirationTimestamp [{}]", 
					this.cacheTTL, expirationTimestamp
					);
		}
		

		private boolean isValid() {
			boolean isValid = (
					expirationTimestamp > Instant.now().getEpochSecond() && !bulkData.isEmpty());
			log.info("Data valid: [{}]", isValid);

			return isValid;
		}
		
		
		private void invalidate() {
			log.debug("Invalidating and evicting cache");
			this.expirationTimestamp = 0L;
			evictData();
		}
		
		
		private List<S> subList(int fromIndex, int toIndex, Class<?>... classes){
			if(classes.length == 0) {
				return this.bulkData.subList(fromIndex, toIndex);
			}
			
			return getIndexedEntries(classes).subList(fromIndex, toIndex);
		}
		
		
		private List<S> getIndexedEntries(Class<?>... classes) {
			List<S> resultList = new LinkedList<>();
			
			if(classIndices.isEmpty()) {
				return resultList;
			}
			
			for(Class<?> c : classes) {
				resultList.addAll(
						classIndices.get(c)
							.stream()
							.map(idx -> bulkData.get(idx))
							.collect(Collectors.toList())
						);
			}
			
			return resultList;
		}

		
		private void setData(List<S> data, Class<?>... classesToIndex) {
			log.debug("Setting [{}] entries, indexing for [{}] classes", 
					data.size(), classesToIndex.length);
			
			this.bulkData = new ArrayList<>(data);

			if(!data.isEmpty()) {
				setClassIndices(data, classesToIndex);
				updateExpirationTimestamp();
			}
		}
		
		
		private void setClassIndices(List<S> data, Class<?>... classes) {
			if(classes.length > 0) {
				
				for(Class<?> c : classes) {
					classIndices.put(c, new LinkedList<>());
				}
				
				int itemsIndexed = 0;
				for(int i = 0; i < data.size(); i++) {
					Class<?> entryClass = data.get(i).getClass();
					if(classIndices.containsKey(entryClass)) {
						classIndices.get(entryClass).add(i);
						itemsIndexed++;
					} else {
						log.info("No mapping requested for [{}]. Skipped", 
								entryClass);
					}
				}
				
				log.debug("Indexed [{}] items for [{}] class(es)", 
						itemsIndexed, classIndices.size());
			} else {
				log.info("No classes passed. Indexing skipped");
			}
		}
		

		private List<S> getData(Class<?>... classes){
			if(classes.length == 0) {
				return bulkData;
			}
			
			return getIndexedEntries(classes);
		}
		

		private boolean isEmpty(Class<?>... classes) {
			if(classes.length == 0) {
				return this.bulkData.isEmpty();
			}
			
			return getIndexedEntries(classes).isEmpty();
		}
		

		private int size(Class<?>...classes) {
			int size = 0;
			
			if(classes.length == 0) {
				size = bulkData.size();
			} else {
				for(Class<?> c : classes) {
					size += classIndices.get(c).size();
				}
			}
			
			return size;
		}
		

		private void evictData() {
			log.debug("Evicting data");
			classIndices.clear();
			bulkData.clear();
		}
		
		
		private void updateExpirationTimestamp() {
			this.expirationTimestamp = Instant.now().getEpochSecond() + cacheTTL;
			log.debug("Expiration timestamp set: [{}]", this.expirationTimestamp);
		}	
	}
	
}

