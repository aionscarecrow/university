package ua.com.foxminded.university.view.paginator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

@DisplayName("Paginator")
class PaginatorTest {

	
	@Nested
	@DisplayName("Setting PageData")
	class SetPageDataTest{
		
		Paginator<Object> paginator = new Paginator<>();
		
		@BeforeEach
		void setUp() {
			paginator = new Paginator<>();
		}
		
		@Test
		@DisplayName("throws with null argument")
		void testThrowsIfNullPassed() {
			assertThrows(IllegalArgumentException.class, () -> paginator.setData(null),
					"should throw IllegalArgumentException if called with null argument");
		}
		
		@Test
		@DisplayName("replaces old data with newly set data")
		void testReplacesOldData() {
			paginator.setItemsPerPage(Integer.MAX_VALUE);
			paginator.setData(Arrays.asList("CAI", "SDI"));
			List<Object> expectedList = Arrays.asList("CAI", "HCF");
			paginator.setData(expectedList);
			List<Object> actualList = paginator.getPageData(1).getPageContent();
			
			assertIterableEquals(expectedList, actualList,
					"should entirely replace old list with one last set");
		}
		
		@Test
		@DisplayName("allows for class-based pagination")
		void testClassIndexing() {
			paginator.setData(
					Arrays.asList("CAI", "SDI", "HCF", Integer.valueOf(1), Integer.valueOf(2)), 
					Integer.class
				);
			
			assertIterableEquals(
					paginator.getPageData(1, Integer.class).getPageContent(),
					Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)),
					"should return Integer entries only"
				);
		}
	}

	
	@Nested
	@DisplayName("Getting PageData")
	class GetPageDataTest{
		
		private Paginator<Member> paginator;
		
		private List<Member> allEntries;
		private Map<Integer, List<Member>> pagedEntries;
		private int elementsCount = 7;
		private int cacheTTL = Integer.MAX_VALUE;
		private int itemsPerPage = 3;
		
		@BeforeEach
		void setUp() throws Exception {
			paginator = new Paginator<>(cacheTTL, itemsPerPage);
			pagedEntries = new HashMap<>();
			
			allEntries = Stream
					.iterate(0, i -> ++i)
					.limit(elementsCount)
					.map(i -> {
						Member member;
						if(ThreadLocalRandom.current().nextInt() % 2 == 0) {
							member = new Student();
						} else {
							member = new Teacher();
						}
						member.setFirstName(Instant.now().getNano() + i + "");
						member.setMemberId(i);
						return member;
					})
					.collect(Collectors.toList());
			
			paginator.setData(allEntries);

			for(int i = 0; i < elementsCount; i++) {
				int currentPage = i / itemsPerPage + 1;
				if(!pagedEntries.containsKey(currentPage)) {
					pagedEntries.put(currentPage, new ArrayList<Member>());
				}
				pagedEntries.get(currentPage).add(allEntries.get(i));
			}
		}
		
		
		@ParameterizedTest
		@ValueSource(classes = {Teacher.class, Student.class})
		@DisplayName("returns paged instances of requested single indexed class")
		void testReturnsRequestedType(Class<?> requestedClass) {
			paginator.setData(allEntries, Teacher.class, Student.class);
			int lastPage = paginator.getPageData(1, requestedClass).getPageCount();
			int page = ThreadLocalRandom.current().nextInt(1, lastPage + 1);
			PageData<Member> members = paginator.getPageData(page, requestedClass);
			
			for(Member current : members.getPageContent()) {
				assertTrue(requestedClass.isInstance(current), 
						"entries should be of class: "	+ requestedClass.getSimpleName());
			}
		}
		
		
		@Test
		@DisplayName("returns paged instances of requested multiple indexed classes")
		void testReturnsRequestedTypes() {
			paginator.setData(allEntries, Teacher.class, Student.class);
			List<Member> requestedEntries = paginator.getData(Teacher.class, Student.class);
			
			assertFalse(allEntries.retainAll(requestedEntries),
					"requested list should contain all previously set entries");
		}
		
		
		@ParameterizedTest
		@ValueSource(classes = {Teacher.class, Student.class})
		@DisplayName("returns empty page if requested class was not indexed")
		void testReturnsEmptyIfUnindexed(Class<?> requestedClass) {	
			PageData<Member> members = paginator.getPageData(1, requestedClass);
			
			assertTrue(members.getPageContent().isEmpty(), "page should be empty");
		}
		
		
		@ParameterizedTest
		@ValueSource(ints = { 1, 2, 3 })
		@DisplayName("returns proper PageData content by page number")
		void testReturnsProperPage(int pageNumber) {
			
			assertIterableEquals(
					pagedEntries.get(pageNumber), 
					paginator.getPageData(pageNumber).getPageContent(),
					"should return proper page content");
		}

		
		@ParameterizedTest
		@ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE})
		@DisplayName("returns nearest valid pageData by invalid page number")
		void testReturnsValidPage(int pageNumber) {
			int expectedPage = pageNumber;
			if(pageNumber < 1) {
				expectedPage = 1;
			} else if (pageNumber > pagedEntries.size()){
				expectedPage = pagedEntries.size();
			}

			assertIterableEquals(
					pagedEntries.get(expectedPage),
					paginator.getPageData(pageNumber).getPageContent(),
					"should cope with invalid page number request providing nearest valid page"
					);
		}
		
		
		@Test
		@DisplayName("returns empty PageData if nothing set")
		void testWhenNothingSet() {
			Paginator<String> emptyPaginator = new Paginator<>();
			int pageNumber = ThreadLocalRandom.current().nextInt();
			
			List<String> expectedList = new ArrayList<>();
			List<String> actualList = 
					emptyPaginator.getPageData(pageNumber).getPageContent();

			assertIterableEquals(expectedList, actualList, 
					"should return empty list when data not set");
		}
		
		
		@Test
		@DisplayName("returns empty PageData if cache invalid")
		void testWhenCacheInvalid() {
			List<String> expectedList = new ArrayList<>();
			int pageNumber = ThreadLocalRandom.current().nextInt();
			paginator.invalidateCache();
			List<Member> actualList = 
					paginator.getPageData(pageNumber).getPageContent();
			
			assertIterableEquals(expectedList, actualList,
					"should return empty list when cache invalid");
		}
	}
	
	
	
	@Nested
	@DisplayName("Cache validation")
	class HasValidCacheTest{
		
		private Paginator<Object> paginator;
		private int cacheTTL = Integer.MAX_VALUE;
		
		@BeforeEach
		void setUp() {
			this.paginator = new Paginator<>(cacheTTL);
		}
		
		
		@Test
		@DisplayName("fails upon initialization")
		void testInitialState() {
			assertFalse(paginator.hasValidCache(), 
					"should initially return false");
		}
		
		
		@Test
		@DisplayName("passes immediately after data was set")
		void testAfterDataSet() {
			paginator.setData(Arrays.asList("CAI", "SDI"));
			assertTrue(paginator.hasValidCache(), 
					"should return true after data's been set");
		}
		
		
		@Test
		@DisplayName("fails if expiration interval < 1 second")
		void testIntervalBelowOneSecond() {
			paginator = new Paginator<>(0);
			assertFalse(paginator.hasValidCache(), 
					"should return false if expiration interval below 1 second");
		}
		
		
		@Test
		@DisplayName("fails after forced invalidation")
		void testForceInvalidation() {
			paginator.setData(Arrays.asList("CAI", "SDI"));
			paginator.invalidateCache();
			assertFalse(paginator.hasValidCache(), 
					"should return false after forced invalidation");
		}
		
		
		@Test
		@DisplayName("fails after empty data was set")
		void testEmptyData() {
			paginator.setData(new ArrayList<>());
			assertFalse(paginator.hasValidCache(), 
					"should return false after setting empty data");
		}
	}
	
}
