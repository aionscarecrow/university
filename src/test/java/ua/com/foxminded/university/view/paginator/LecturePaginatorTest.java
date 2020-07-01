package ua.com.foxminded.university.view.paginator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.foxminded.university.domain.entities.Lecture;

@DisplayName("Lecture paginator")
class LecturePaginatorTest {
	
	EntryQueryablePaginator<Lecture> paginator;
	List<Lecture> lectures;

	@BeforeEach
	void setUp() {
		paginator = new LecturePaginator<>();
		lectures = new LinkedList<>();
		for(int i = 0; i < 5; i++) {
			Lecture lecture = new Lecture();
			lecture.setLectureId(i + 1);
			lectures.add(lecture);
		}
		paginator.setData(lectures);
	}
	
	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4, 5})
	@DisplayName("Queries entry by id")
	void testGetEntry(int expectedId) {
		int actualId = paginator.getEntry(expectedId).get().getLectureId();
		assertEquals(expectedId, actualId);
	}
	
	
	@Test
	@DisplayName("Returns first found entry")
	void testGetsFirstFound() {
		int id = lectures.size();
		Lecture addedLecture = new Lecture();
		addedLecture.setLectureId(id);
		lectures.add(addedLecture);
		paginator.setData(lectures);
		
		Lecture firstFoundLecture = paginator.getEntry(id).get();
		assertFalse(addedLecture == firstFoundLecture,
				"should not be the same lecture");
	}
	
	
	@Test
	@DisplayName("Returns empty Optional if no entry found")
	void testIfNotFound() {
		Optional<Lecture> lecture = paginator.getEntry(Integer.MAX_VALUE);
		assertFalse(lecture.isPresent());
	}
}
