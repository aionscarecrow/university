package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.UniversityDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.exceptions.ServiceException;


@ExtendWith(MockitoExtension.class)
@DisplayName("University Service")
class UniversityServiceImplTest {
	
	@Mock 
	UniversityDao universityDao; 
	
	UniversityService universityService;
	University university;
	Lecture lecture;
	Member teacher; 
	
	
	@BeforeEach
	void setUp() throws Exception {
		universityService = new UniversityServiceImpl(universityDao);
	}
	
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves university object")
		void testRetrieveUniversity() throws DaoException, ServiceException {
			university = new University();
			when(universityDao.retrieveUniversity()).thenReturn(university);
			
			University actualUniversity = universityService.retrieveUniversity();
			assertTrue(
					university.equals(actualUniversity), 
					"should return expected university object"
					);
		}
		
		@Test
		@DisplayName("retrieves monthly list of lectures")
		void testRetrieveMonthlySchedule() throws ServiceException {
			lecture = new Lecture();
			teacher = new Teacher("Gustavo", "Fring");
			teacher.setMemberId(999);
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(universityDao.retrieveMonthlySchedule(any(Member.class)))
					.thenReturn(lectures);
			
			List<Lecture> actualSchedule = universityService.retrieveMonthlySchedule(teacher);
			assertTrue(
					lectures.equals(actualSchedule), 
					"should return correct lecture list"
					);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testRetrieveMonthlyScheduleThrowsIfNull() {
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveMonthlySchedule(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if member id invalid")
		void testRetrieveMonthlyScheduleThrowsIfIdInvalid() {
			teacher = new Teacher("Brian", "Moser");
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveMonthlySchedule(teacher),
					"should throw ServiceException if argument is null"
					);
		}

		@Test
		@DisplayName("retrieves daily list of lectures")
		void testRetrieveDailySchedule() throws ServiceException {
			lecture = new Lecture();
			lecture.setLectureId(999);
			teacher = new Teacher("Gustavo", "Fring");
			teacher.setMemberId(999);
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(universityDao.retrieveDailySchedule(any(Member.class)))
					.thenReturn(lectures);
			
			List<Lecture> actualSchedule = universityService.retrieveDailySchedule(teacher);
			assertTrue(
					lectures.equals(actualSchedule), 
					"should return correct lecture list"
					);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testRetrieveDailyScheduleThrowsIfNull() {
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveDailySchedule(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if member id invalid")
		void testRetrieveDailyScheduleThrowsIfIdInvalid() {
			teacher = new Teacher("Brian", "Moser");
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveDailySchedule(teacher),
					"should throw ServiceException if argument is null"
					);
		}
	}
	
	
	@Nested
	@DisplayName("AddMember")
	class AddMemberTest {
		
		@Test
		@DisplayName("calls method to add member")
		void testAddMember() throws ServiceException, DaoException {
			teacher = new Teacher("Gustavo", "Fring");
			teacher.setMemberId(999);
			universityService.addMember(teacher);
			verify(universityDao).addMember(teacher);
		}
		
		
		@Test
		@DisplayName("throws if member null")
		void testAddMemberThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> universityService.addMember(null),
					"should throw ServiceException if member is null"
					);
		}
		
		@Test
		@DisplayName("throws if member id invalid")
		void testAddMemberThrowsIfIdInvalid() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(0);
			assertThrows(
					ServiceException.class, 
					() -> universityService.addMember(student),
					"should throw ServiceException if member id invalid"
					);
		}
		
	}

	
	@Nested
	@DisplayName("ScheduleLecture")
	class ScheduleLectureTest {
		
		@Test
		@DisplayName("calls method to schedule lecture")
		void testScheduleLecture() throws ServiceException, DaoException {
			lecture = new Lecture();
			universityService.scheduleLecture(lecture);
			verify(universityDao).scheduleLecture(lecture);
		}
		
		@Test
		@DisplayName("throws if lecture is null")
		void testScheduleLectureThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> universityService.scheduleLecture(null),
					"should throw ServiceException if argument is null"
					);
		} 
	}

	
	@Nested
	@DisplayName("CancelLecture")
	class CancelLectureTest {
		
		@Test
		@DisplayName("calls method to cancel scheduled lecture")
		void testCancelLecture() throws ServiceException, DaoException {
			lecture = new Lecture();
			lecture.setLectureId(ThreadLocalRandom.current().nextInt(2, 99));
			universityService.cancelLecture(lecture);
			verify(universityDao).cancelLecture(lecture);
		}
		
		@Test
		@DisplayName("throws if lecture is null")
		void testCancelLectureThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> universityService.cancelLecture(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testCancelLectureThrowsIfIdInvalid() {
			lecture = new Lecture();
			lecture.setLectureId(0);
			assertThrows(
					ServiceException.class, 
					() -> universityService.cancelLecture(lecture),
					"should throw ServiceException if lecture id invalid"
					);
		} 
	}
		
}
