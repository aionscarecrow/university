package ua.com.foxminded.university.view.paginator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.foxminded.university.domain.entities.Course;

@DisplayName("Course paginator")
class CoursePaginatorTest {
	
	EntryQueryablePaginator<Course> paginator;
	List<Course> courses;

	@BeforeEach
	void setUp() {
		paginator = new CoursePaginator<>();
		courses = new LinkedList<>();
		for(int i = 0; i < 5; i++) {
			Course course = new Course();
			course.setCourseId(i + 1);
			courses.add(course);
		}
		paginator.setData(courses);
	}

	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4, 5})
	@DisplayName("Queries entry by id")
	void testGetEntry(int expectedId) {
		int actualId = paginator.getEntry(expectedId).get().getCourseId();
		assertEquals(expectedId, actualId, 
				"requested id and id of obtained entry should match");
	}
	
	
	@Test
	@DisplayName("Returns first found entry")
	void testGetsFirstFound() {
		int id = courses.size();
		Course addedCourse = new Course();
		addedCourse.setCourseId(id);
		courses.add(addedCourse);
		paginator.setData(courses);
		
		Course firstFoundCourse = paginator.getEntry(id).get();
		assertFalse(addedCourse == firstFoundCourse,
				"should not be the same course");
	}
	
	@Test
	@DisplayName("Returns empty Optional if no entry found")
	void testIfNotFound() {
		Optional<Course> course = paginator.getEntry(Integer.MAX_VALUE);
		assertFalse(course.isPresent());
	}

}
