package ua.com.foxminded.university.view.paginator;

import java.util.List;
import java.util.Optional;

public interface EntryQueryablePaginator<T> {
	
	Optional<T> getEntry(int key);
	
	PageData<T> getPageData(final int pageNumber, Class<?>...classes);
	
	boolean hasValidCache();
	
	void invalidateCache();
	
	int getItemsPerPage();
	
	void setData(List<T> data, Class<?>... classes);

	void setItemsPerPage(int itemsPerPage);
	
}
