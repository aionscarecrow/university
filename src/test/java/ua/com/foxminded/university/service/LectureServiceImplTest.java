package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.repository.LectureRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.validators.LectureValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lecture Service")
class LectureServiceImplTest {
	
	@Mock
	LectureRepository repository;
	
	@Mock(name = "lectureValidator")
	LectureValidator validator;
	
	@InjectMocks
	LectureService lectureService = new LectureServiceImpl();
	
	Lecture lecture;


	@BeforeEach
	void setUp() throws Exception {
		lecture = new Lecture();
		lecture.setLectureId(999);
	}
	
	
	@Nested
	@DisplayName("Create")
	class CreateTest {
		
		@Test
		@DisplayName("calls creation method")
		void testCreate() throws ServiceException {
			lectureService.create(lecture);
			verify(repository).save(lecture);
		}
		
		@Test
		@DisplayName("calls for proper parameter validation")
		void testCallsForValidation() throws ServiceException {
			lectureService.create(lecture);
			verify(validator).validateCreateable(lecture);
		}
	}
	
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves Lecture object by id")
		void testRetrieveById() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(lecture));
			
			Lecture actualLecture = lectureService.retrieveById(999);
			assertEquals(lecture, actualLecture, 
					"should return correct lecture object by id");
		}
		
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(lecture));
			lectureService.retrieveById(1);
			
			verify(validator).validateId(1);
		}
	}
	
	
	@Nested
	@DisplayName("RetrieveAll")
	class RetrieveAllTest {
		
		@Test
		@DisplayName("retrieves list of lectures")
		void testRetrieveAll() throws ServiceException {
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(repository.findAllByOrderByLectureIdAsc()).thenReturn(lectures);
			
			List<Lecture> actualLectures = lectureService.retrieveAll();
			assertArrayEquals(
					lectures.toArray(), 
					actualLectures.toArray(), 
					"should return correct lectures list"
				);
		}
	}
	
	
	@Nested
	@DisplayName("Update")
	class UpdateTest {
		
		@Test
		@DisplayName("calls method to update lecture")
		void testUpdate() throws ServiceException {
			lectureService.update(lecture);
			verify(repository).save(lecture);
		}
					
		@Test
		@DisplayName("calls for proper parameter validation ")
		void testCallsForValidation() throws ServiceException {
			lectureService.update(lecture);
			verify(validator).validateUpdatable(lecture);;
		}
	}

	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete lecture")
		void testDelete() throws ServiceException {
			lectureService.delete(lecture);
			verify(repository).delete(lecture);
		}
		
		@Test
		@DisplayName("calls for proper parameter validation")
		void testCallsForValidation() throws ServiceException {
			lectureService.delete(lecture);
			verify(validator).validateDeletable(lecture);
		}
	}

}
