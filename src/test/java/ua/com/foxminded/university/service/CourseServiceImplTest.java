package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
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
import ua.com.foxminded.university.repository.CourseRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.validators.EntityValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Service")
class CourseServiceImplTest {
	
	@Mock
	CourseRepository repository;
	
	@Mock(name = "courseValidator")
	EntityValidator<Course> validator;
	
	@InjectMocks
	CourseService courseService = new CourseServiceImpl();
	
	Course course;

	
	@BeforeEach
	void setUp() throws Exception {
		course = new Course("Subject", "Description");
		course.setCourseId(999);
	}
	
	@Nested
	@DisplayName("Create")
	class CreationTest {
		
		@Test
		@DisplayName("calls method to create course")
		void testCreate() throws ServiceException {
			courseService.create(course);
			verify(repository).save(course);
		}
		
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			courseService.create(course);
			verify(validator).validateCreateable(course);
		}
	}
	
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves Course object by id")
		void testRetrieveById() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(course));
			Course actualCourse = courseService.retrieveById(5);
			assertEquals(course, actualCourse, "sould return correct course object by id");
		}
		
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(course));
			courseService.retrieveById(1);
			verify(validator).validateId(1);
		}
		
		@Test
		@DisplayName("throws if null retrieved")
		void testThrowsUponNull() {
			assertThrows(
					ServiceException.class,
					() -> courseService.retrieveById(99),
					"Should throw if null retrieved"
					);
		}
	}
	
	
	@Nested
	@DisplayName("RetrieveAll")
	class RetrieveAllTest {
		
		@Test
		@DisplayName("retrieves list of courses")
		void testRetrieveAll() throws ServiceException {
			List<Course> courses = Collections.nCopies(2, course);
			when(repository.findAllByOrderByCourseIdAsc()).thenReturn(courses);
			
			List<Course> actualCourses = courseService.retrieveAll();
			assertArrayEquals(courses.toArray(), actualCourses.toArray());
		}
	}
	
	
	@Nested
	@DisplayName("Update")
	class UpdateTest {
		
		@Test
		@DisplayName("calls method to update course")
		void testUpdate() throws ServiceException {
			courseService.update(course);
			verify(repository).save(course);
		}
		
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			courseService.update(course);
			verify(validator).validateUpdatable(course);
		}
	}
	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete course")
		void testDelete() throws ServiceException {
			courseService.delete(course);
			verify(repository).delete(course);
		}
		
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			courseService.delete(course);
			verify(validator).validateDeletable(course);
		}
	}
	

}
