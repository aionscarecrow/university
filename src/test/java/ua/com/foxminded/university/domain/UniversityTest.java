package ua.com.foxminded.university.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.LectureSchedule;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.domain.exceptions.DomainException;

@DisplayName("University")
class UniversityTest {
	
	University university;
	LocalDateTime currentDateTime;
	Lecture predefinedLecture;
	Member teacher;
	Member student1;
	Member student2;
	Course course;


	@BeforeEach
	void setUp() throws Exception {
		
		university = new University();
		predefinedLecture = new Lecture();
		course = new Course("Glass blowing", "The technique of inflating molten glass");
		course.setCourseId(1);
		teacher = new Teacher("Marla", "Singer");
		teacher.setMemberId(100);
		student1 = new Student("Ralph", "Cifaretto");
		student1.setMemberId(99);
		student2 = new Student("Charmaine", "Bucco");
		student2.setMemberId(98);
		
		currentDateTime = LocalDateTime.now();
		predefinedLecture.setDate(currentDateTime);
		predefinedLecture.setTeacher((Teacher)teacher);
		predefinedLecture.addStudent((Student)student1);
		predefinedLecture.addStudent((Student)student2);
		predefinedLecture.setCourse(course);
		predefinedLecture.setLectureId(1);
	}
	
	@Nested
	@DisplayName("Adding Member")
	class AddMemberTest {
		
		@Test
		@DisplayName("adds Members")
		void testAddMember() throws DomainException {
			university.addMember(teacher);
			university.addMember(student1);
			
			assertTrue(university.getTeachers().size() == 1, "should have one teacher");
			assertTrue(university.getStudents().size() == 1, "should have one student");
		}
		
		@Test
		@DisplayName("throws if invalid member type")
		void testAddMemberThrowsIfMemberInvalid() {
			assertThrows(
					DomainException.class,
					() -> university.addMember(new Member()),
					"should throw DomainException if invalid member type"
					);
		}
	}
	
	@Nested
	@DisplayName("Set Data")
	class SetDataTest {
		
		@Test
		@DisplayName("sets fields from UniversityData")
		void testSetData() {
			UniversityData data = new UniversityData();
			data.setStudents(Arrays.asList((Student)student1, (Student)student2));
			data.setLectures(Arrays.asList(predefinedLecture));
			data.setTeachers(Arrays.asList((Teacher)teacher));
			university.setData(data);
			LectureSchedule monthlySchedule = university.getMonthlySchedule(teacher);
			
			assertTrue(university.getStudents().size() == 2, "should have two students");
			assertTrue(university.getTeachers().size() == 1, "should have one teacher");
			assertTrue(monthlySchedule.getLectures().size() == 1, "should have one lecture");
		}
	}

	@Nested
	@DisplayName("Lecture Scheduling")
	class SchedulingTest {
		
		@Test
		@DisplayName("schedules lecture")
		void testSchedule() throws DomainException {
			university.scheduleLecture(predefinedLecture);
			assertFalse(
					university.getMonthlySchedule(teacher).toString().equals(""),
					"lecture schedule should not be empty"
					);
		}
		
		@Test
		@DisplayName("throws if duplicate lecture passed")
		void testScheduleThrowsIfDuplicate() throws DomainException {
			university.scheduleLecture(predefinedLecture);
			assertThrows(
					DomainException.class, 
					() -> university.scheduleLecture(predefinedLecture),
					"should throw DomainException if duplicate lecture"
					);

		}
		
		@Test
		@DisplayName("throws if empty lecture passed")
		void testScheduleThrowsIfEmpty() throws DomainException {
			university.scheduleLecture(predefinedLecture);
			assertThrows(
					DomainException.class, 
					() -> university.scheduleLecture(new Lecture()),
					"should throw DomainException if passed empty lecture"
					);

		}
		
		@Test
		@DisplayName("throws if lecture is null")
		void testScheduleThrowsIfNull() throws DomainException {
			assertThrows(
					DomainException.class, 
					() -> university.scheduleLecture(null),
					"should throw DomainException if arguement null"
					);
		}
		
		@Test
		@DisplayName("teacher schedule conflict")
		void testTeacherTimeConflict() throws DomainException {
			Lecture badLecture = new Lecture();
			badLecture.setDate(currentDateTime);
			badLecture.setCourse(course);
			badLecture.setTeacher((Teacher)teacher);
			badLecture.addStudent((Student)student1);
			
			university.scheduleLecture(predefinedLecture);
			assertThrows(
					DomainException.class, 
					() -> university.scheduleLecture(badLecture), 
					"should throw DomainException if teacher time overlaps"
					);
		}
	}
	
	@Nested
	@DisplayName("Lecture cancelling")
	class CancellingTest {
		
		@Test
		@DisplayName("cancels lecture")
		void testCancel() throws DomainException {
			university.scheduleLecture(predefinedLecture);
			university.cancelLecture(predefinedLecture);
			assertTrue(
					university.getMonthlySchedule(student1).toString().equals(""),
					"lecture schedule should be empty");
		}
		
		@Test
		@DisplayName("throws if non-scheduled lecture passed")
		void testCancelNonScheduled() throws DomainException {
			Lecture unscheduled = new Lecture();
			unscheduled.setLectureId(ThreadLocalRandom.current().nextInt(2, 99));
			unscheduled.setCourse(new Course());
			unscheduled.setDate(currentDateTime.withHour(1));
			unscheduled.setTeacher((Teacher)teacher);
			unscheduled.addStudent((Student)student2);
			
			assertThrows(
					DomainException.class, 
					() -> university.cancelLecture(unscheduled),
					"should throw DomainException if lecture not scheduled"
					);
		}
		
		@Test
		@DisplayName("throws if argument null")
		void testCancelNull() throws DomainException {
			assertThrows(
					DomainException.class,
					() -> university.cancelLecture(null),
					"should throw DomainException if null passed"
					);
		}
	}
	
	@Nested
	@DisplayName("Schedule retrieval tests")
	class RetrievalTest {
		
		Lecture lecture2;
		
		@BeforeEach
		void setUp() throws Exception {
			lecture2 = new Lecture();
			lecture2.setTeacher((Teacher)teacher);
			lecture2.addStudent((Student)student1);
			lecture2.addStudent((Student)student2);
			lecture2.setCourse(course);
		}
		
		@Test
		@DisplayName("retrieves daily lectures")
		void testDailySchedule() throws DomainException {
			
			String expectedString = "Lecture ID: 1\n" + 
					"Date: " + currentDateTime + "\n" + 
					"Course: [courseId=1, subject=Glass blowing, description="
					+ "The technique of inflating molten glass]\n" + 
					"Teacher: [memberId=100, typeId=1, firstName=Marla, lastName=Singer]\n" + 
					"Student: [memberId=99, typeId=2, firstName=Ralph, lastName=Cifaretto]\n" + 
					"Student: [memberId=98, typeId=2, firstName=Charmaine, lastName=Bucco]\n" + 
					"\n";
			
			lecture2.setDate(currentDateTime.plusDays(1));
			university.scheduleLecture(predefinedLecture);
			university.scheduleLecture(lecture2);
			assertTrue(
					university.getDailySchedule(student1).toString().equals(expectedString),
					"only today's lectures should be listed");
		}
		
		@Test
		@DisplayName("retrieves monthly lectures")
		void testMonthlySchedule() throws DomainException {
			lecture2.setDate(currentDateTime.plusMonths(1));
			university.scheduleLecture(lecture2);
			assertTrue(
					university.getMonthlySchedule(teacher).toString().equals(""),
					"only current month's lectures should be listed");
		}
	}
}
