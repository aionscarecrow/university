package ua.com.foxminded.university.view.paginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageData <T>{
	
	private int pageNumber;
	private int pageCount;
	private List<T> pageContent;
	
	public PageData() {
		this.pageNumber = 0;
		this.pageContent = new ArrayList<>();
	}


	public int getPageNumber() {
		return pageNumber;
	}

	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	
	public List<T> getPageContent() {
		return Collections.unmodifiableList(pageContent);
	}


	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}


	public int getPageCount() {
		return pageCount;
	}
	
	
	public void setPageContent(List<T> pageContent) {
		this.pageContent = pageContent;
	}
	
	public int getPageSize() {
		return pageContent.size();
	}

	
	@Override
	public String toString() {
		return "PageData ["
				+ "pageNumber=" + pageNumber + 
				", pageContent size: " + pageContent.size() +
				", \ncontent=" + pageContent +
				"]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageContent == null) ? 0 : pageContent.hashCode());
		result = prime * result + pageCount;
		result = prime * result + pageNumber;
		return result;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageData other = (PageData) obj;
		if (pageContent == null) {
			if (other.pageContent != null)
				return false;
		} else if (!pageContent.equals(other.pageContent))
			return false;
		if (pageCount != other.pageCount)
			return false;
		if (pageNumber != other.pageNumber)
			return false;
		return true;
	}
	
	
}
