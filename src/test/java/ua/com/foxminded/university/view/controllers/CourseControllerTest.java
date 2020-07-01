package ua.com.foxminded.university.view.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.EntryQueryablePaginator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Controller")
class CourseControllerTest {
	
	@Mock(name="courseService")
	private CourseService courseService;
	
	@Mock(name="coursePaginator")
	private EntryQueryablePaginator<Course> pagination;
	
	@InjectMocks
	private CourseController courseController = new CourseController();

	@Nested
	@DisplayName("List Courses")
	class ListCoursesTest {
		
		@Test
		@DisplayName("returns proper view")
		public void testReturnProperView() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			String expectedView = CourseController.TABLE_FRAGMENT;
			String actualView = 
					courseController.listCourses(1, null).getViewName();
			
			assertEquals(expectedView, actualView);
		}
	}
	
	@Nested
	@DisplayName("Get courses page data")
	class getCoursesPageDataTest {
		
		@Test
		@DisplayName("calls service with fetch param present")
		public void testCallToService() throws ServiceException {
			courseController.getCoursesPageData(1, "");
			
			verify(courseService).retrieveAll();
		}
		
		
		@Test
		@DisplayName("doesn't call service with fetch=null if cache valid")
		public void testNoCallToService() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			courseController.getCoursesPageData(1, null);
			
			verify(courseService, times(0)).retrieveAll();
		}
		
		
		@Test
		@DisplayName("calls service with fetch=null if cache invalid")
		public void testCallServiceIfInvalidCache() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(false);
			courseController.getCoursesPageData(1, null);
			
			verify(courseService, times(1)).retrieveAll();
		}
	}
	
	@Nested
	@DisplayName("Get course by id")
	class GetCoruseByIdTest {
		
		@Test
		@DisplayName("queries paginator for entry if cache valid")
		void testCallsPaginatorIfCacheValid() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			courseController.getCourseById(1);
			
			verify(pagination).getEntry(1);
		}
		
		
		@Test
		@DisplayName("calls service if cache invalid")
		void testCallsServiceIfCacheInvalid() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(false);
			courseController.getCourseById(1);
			
			verify(courseService).retrieveById(1);
		}
	}
	
	@Nested
	@DisplayName("Get form")
	class GetFormTest {
		
		Course course;
		
		@BeforeEach
		void setUp() {
			course = new Course();
			course.setCourseId(1);
		}
		
		
		@Test
		@DisplayName("requests course if id > 0")
		void testRequestLectureIfIdSet() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			courseController.getForm(course.getCourseId(), Optional.of(1));
			
			verify(pagination).getEntry(anyInt());
		}
		
		
		@Test
		@DisplayName("does not request course if id == 0")
		void testNoMemberRequestIfNoIdSet() throws ServiceException {
			course.setCourseId(0);
			courseController.getForm(course.getCourseId(), Optional.of(1));
			
			verify(pagination, never()).getEntry(anyInt());
		}
		
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = CourseController.EDIT_FRAGMENT;
			String actualView = courseController.getForm(
					course.getCourseId(), Optional.of(1)).getViewName();
			
			assertEquals(expectedView, actualView,
					"should return proper course form view");
		}
	}
	
	@Nested
	@DisplayName("Save course")
	class testSave {
		
		Course course;
		
		@BeforeEach
		void setUp() {
			course = new Course();
			course.setCourseId(1);
		}
		
		
		@Test
		@DisplayName("calls update method for existent course")
		void testUpdatesExistent() throws ServiceException {
			courseController.save(Optional.of(1), course);
			
			verify(courseService).update(course);
		}
		
		
		@Test
		@DisplayName("calls create method for nonexistent course")
		void testCreatesNonexistent() throws ServiceException {
			course.setCourseId(0);
			courseController.save(Optional.of(1), course);
			
			verify(courseService).create(course);
		}
		
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			courseController.save(Optional.of(1), course);
			
			verify(pagination).invalidateCache();
		}
		
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = CourseController.TABLE_FRAGMENT;
			String actualView = 
					courseController.save(Optional.of(1), course).getViewName();
			
			assertEquals(expectedView, actualView,
					"should return proper course list view");
		}
	}
	
	@Nested
	@DisplayName("Delete course")
	class deleteTest {
		
		Course course;
		
		@BeforeEach
		void setUp() {
			course = new Course();
			course.setCourseId(1);
		}
		
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = CourseController.TABLE_FRAGMENT;
			String actualView =	courseController.
					delete(course.getCourseId(), Optional.of(1)).getViewName();
			
			assertEquals(expectedView, actualView, 
					"sould return proper course list view");
		}
		
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			courseController.delete(course.getCourseId(), Optional.of(1));
			
			verify(pagination).invalidateCache();
		}
		
		
		@Test
		@DisplayName("calls delete method")
		void testCallsDelete() throws ServiceException {
			courseController.delete(course.getCourseId(), Optional.of(1));
			
			verify(courseService).delete(course);
		}
	}
	

}