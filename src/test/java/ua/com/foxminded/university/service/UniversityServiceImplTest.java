package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.domain.exceptions.DomainException;
import ua.com.foxminded.university.service.exceptions.ServiceException;


@ExtendWith(MockitoExtension.class)
@DisplayName("University Service")
class UniversityServiceImplTest {
	
	@Mock
	MemberService memberService;
	
	@Mock
	LectureService lectureService;
	
	@InjectMocks
	UniversityService universityService = new UniversityServiceImpl();
	
	
	@Nested
	@DisplayName("Retrieve University")
	class RetrieveUniversityTest {
		
		List<Member> members;
		Student student = new Student();
		Teacher teacher = new Teacher();
		
		@BeforeEach
		void setUp() throws ServiceException {
			members = Arrays.asList(student, teacher);
			when(memberService.retrieveAll()).thenReturn(members);
			when(lectureService.retrieveAll()).thenReturn(new LinkedList<Lecture>());
		}
		
		@Test
		@DisplayName("requests members from member service")
		void testRequestsMembersFromService() throws ServiceException {
			universityService.retrieveUniversity();
			verify(memberService).retrieveAll();
		}
		
		@Test
		@DisplayName("requests lectures from lecture service")
		void testRequestsLecturesFromService() throws ServiceException {
			universityService.retrieveUniversity();
			verify(lectureService).retrieveAll();
		}
		
		@Test
		@DisplayName("returns proper university object")
		void testReturnsProperUniversity() throws DomainException, ServiceException {
			University expectedUniversity = new University();
			expectedUniversity.addMember(teacher);
			expectedUniversity.addMember(student);
			University actualUniversity = universityService.retrieveUniversity();
			
			assertEquals(
					expectedUniversity, actualUniversity, 
					"should return proper university object");
		}
		
		@Test
		@DisplayName("handles empty university data")
		void testBuildsFromEmptyData() throws ServiceException {
			when(memberService.retrieveAll()).thenReturn(new LinkedList<Member>());
			University expectedUniversity = new University();
			University actualUniversity = universityService.retrieveUniversity();
			
			assertEquals(
					expectedUniversity, actualUniversity, 
					"should return proper university object");
		}
	}
	
	
	@Nested
	@DisplayName("Schedule Lecture")
	class ScheduleLectureTest {
		
		@Test
		@DisplayName("calls lecture service to schedule lecture")
		void testCallsServiceToScheduleLecture() throws ServiceException {
			Lecture lecture = new Lecture();
			universityService.scheduleLecture(lecture);
			
			verify(lectureService).create(lecture);
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
	@DisplayName("Cancel Lecture")
	class CancelLecture {
		
		@Test
		@DisplayName("calls lecture service to cancel lecture")
		void testCallsServiceToCancelLecture() throws ServiceException {
			Lecture lecture = new Lecture();
			lecture.setLectureId(1);
			universityService.cancelLecture(lecture);
			
			verify(lectureService).delete(lecture);
		}
		
		@Test
		@DisplayName("throws if lecture id not set")
		void testThrowsIfNoIdSet() throws ServiceException {
			Lecture lecture = new Lecture();
			
			assertThrows(
					ServiceException.class,
					() -> universityService.cancelLecture(lecture),
					"should throw exception if lecture id not set"
				);
		}
		
		@Test
		@DisplayName("throws if lecture is null")
		void testThrowsIfNullPassed() throws ServiceException {
			Lecture lecture = null;
			
			assertThrows(
					ServiceException.class,
					() -> universityService.cancelLecture(lecture),
					"should throw exception if lecture is null"
				);
		}
	}
	
	
	@Nested
	@DisplayName("Add Member")
	class AddMember {
		
		@Test
		@DisplayName("calls member service to add member")
		void testCallsServiceToAddMember() throws ServiceException {
			Member member = new Member();
			member.setMemberId(1);
			universityService.addMember(member);
			
			verify(memberService).create(member);
		}
		
		@Test
		@DisplayName("throws if member is null")
		void testThrowsIfNullPassed() throws ServiceException {
			Member member = null;
			
			assertThrows(
					ServiceException.class,
					() -> universityService.addMember(member),
					"should throw exception if member is null"
				);
		}
	}
	
	
	@Nested
	@DisplayName("Retrieve Schedule")
	class RetrieveScheduleTest {
		
		@Test
		@DisplayName("retrieves monthly list of lectures")
		void testRetrieveMonthlySchedule() throws ServiceException {
			Lecture lecture = new Lecture();
			Teacher teacher = new Teacher("Gustavo", "Fring");
			teacher.setMemberId(999);
			lecture.setTeacher(teacher);
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(lectureService.retrieveMonthlyLectures()).thenReturn(lectures);
			
			List<Lecture> actualSchedule = 
					universityService.retrieveMonthlySchedule(teacher);
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
			Teacher teacher = new Teacher("Brian", "Moser");
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveMonthlySchedule(teacher),
					"should throw ServiceException if argument is null"
					);
		}
		
		
		@Test
		@DisplayName("retrieves daily list of lectures")
		void testRetrieveDailySchedule() throws ServiceException {
			Lecture lecture = new Lecture();
			lecture.setLectureId(999);
			Teacher teacher = new Teacher("Gustavo", "Fring");
			teacher.setMemberId(999);
			lecture.setTeacher(teacher);
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(lectureService.retrieveDailyLectures()).thenReturn(lectures);
			
			List<Lecture> actualSchedule = 
					universityService.retrieveDailySchedule(teacher);
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
			Teacher teacher = new Teacher("Brian", "Moser");
			assertThrows(
					ServiceException.class,
					() -> universityService.retrieveDailySchedule(teacher),
					"should throw ServiceException if argument is null"
					);
		}
		
	}
		
}
