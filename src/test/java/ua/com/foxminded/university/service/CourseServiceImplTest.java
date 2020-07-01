package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Service")
class CourseServiceImplTest {
	
	@Mock
	private CourseDao courseDao;
	
	private CourseService courseService;
	private Course course;

	
	@BeforeEach
	void setUp() throws Exception {
		courseService = new CourseServiceImpl(courseDao);
		course = new Course("Subject", "Description");
		course.setCourseId(999);
	}
	
	@Nested
	@DisplayName("Create")
	class CreationTest {
		
		@Test
		@DisplayName("calls method to create course")
		void testCreate() throws ServiceException, DaoException {
			courseService.create(course);
			verify(courseDao).create(course);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testCreateThrowsIfNull() {
			assertThrows(ServiceException.class, () -> courseService.create(null),
					"should throw ServiceException if argument is null");
		}
	}
	
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves Course object by id")
		void testRetrieveById() throws ServiceException {
			when(courseDao.retrieveById(anyInt())).thenReturn(course);
			Course actualCourse = courseService.retrieveById(5);
			assertEquals(course, actualCourse, "sould return correct course object by id");
		}
		
		@Test
		@DisplayName("throws if invalid id passed")
		void testRetrieveThrowsIfIdInvalid() {
			assertThrows(ServiceException.class, () -> courseService.retrieveById(0),
					"should throw ServiceException if id invalid");
		}
		
		@Test
		@DisplayName("retrieves list of courses")
		void restRetrieveAll() throws ServiceException {
			List<Course> courses = Collections.nCopies(2, course);
			when(courseDao.retrieveAll()).thenReturn(courses);
			
			List<Course> actualCourses = courseService.retrieveAll();
			assertArrayEquals(courses.toArray(), actualCourses.toArray());
		}
	}
	
	@Nested
	@DisplayName("Update")
	class UpdateTest {
		
		@Test
		@DisplayName("calls method to update course")
		void testUpdate() throws ServiceException, DaoException {
			courseService.update(course);
			verify(courseDao).update(course);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testUpdateThrowsIfNull() {
			assertThrows(ServiceException.class, () -> courseService.update(null),
					"should throw ServiceException if argument is null");
		}
		
		@Test
		@DisplayName("throws if course id invalid")
		void testUpdateThrowsIfIdInvalid() {
			course.setCourseId(0);
			assertThrows(ServiceException.class, () -> courseService.update(course),
					"should throw ServiceException if course id field not set");
		}
	}
	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete course")
		void testDelete() throws ServiceException, DaoException {
			courseService.delete(course);
			verify(courseDao).delete(course);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testDeleteThrowsIfNull() {
			assertThrows(ServiceException.class, () -> courseService.delete(null),
					"should throw ServiceException if argument is null");
		}
		
		@Test
		@DisplayName("throws if course id invalid")
		void testDeleteThrowsIfIdInvalid() {
			course.setCourseId(0);
			assertThrows(ServiceException.class, () -> courseService.delete(course),
					"should throw ServiceException if course id field not set");
		}
	}
	

}
