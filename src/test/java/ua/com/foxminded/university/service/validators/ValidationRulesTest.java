package ua.com.foxminded.university.service.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.validators.ValidationRules.CourseRules;
import ua.com.foxminded.university.service.validators.ValidationRules.LectureRules;
import ua.com.foxminded.university.service.validators.ValidationRules.MemberRules;

class ValidationRulesTest {

	@Nested
	@DisplayName("Member rules")
	class MemberRulesTest {
		
		Member member;
		
		Predicate<Member> hasId = MemberRules.HAS_ID.getRule().getDefinition();
		Predicate<Member> hasFirstName = MemberRules.HAS_FIRST_NAME.getRule().getDefinition();
		Predicate<Member> hasLastName = MemberRules.HAS_LAST_NAME.getRule().getDefinition();
		Predicate<Member> hasTypeId = MemberRules.HAS_TYPE_ID.getRule().getDefinition();

		@BeforeEach
		void setUp() {
			member = new Member();
		}
		
		
		@Test
		@DisplayName("return false if member fields not set")
		void testInvalidMember() {
			String message = "Should return false";
			assertFalse(hasId.test(member), message);
			assertFalse(hasFirstName.test(member), message);
			assertFalse(hasLastName.test(member), message);
			assertFalse(hasTypeId.test(member), message);
		}
		
		
		@Test
		@DisplayName("return true if member fields set")
		void testValidMember() {
			String message = "Should return true";
			member.setMemberId(1);
			member.setTypeId(1);
			member.setFirstName("John");
			member.setLastName("Carmack");
			
			assertTrue(hasId.test(member), message);
			assertTrue(hasFirstName.test(member), message);
			assertTrue(hasLastName.test(member), message);
			assertTrue(hasTypeId.test(member), message);
		}
	}
	
	
	@Nested
	@DisplayName("Lecture rules")
	class LectureRulesTest {
		
		Lecture lecture;
		
		Predicate<Lecture> hasId = 
				LectureRules.HAS_ID.getRule().getDefinition();
		Predicate<Lecture> hasDate = 
				LectureRules.HAS_DATE.getRule().getDefinition();
		Predicate<Lecture> hasCourse = 
				LectureRules.HAS_COURSE.getRule().getDefinition();
		Predicate<Lecture> hasTeacher = 
				LectureRules.HAS_TEACHER.getRule().getDefinition();
		Predicate<Lecture> validTeacher = 
				LectureRules.HAS_VALID_TEACHER.getRule().getDefinition();
		Predicate<Lecture> validStudents = 
				LectureRules.HAS_VALID_STUDENTS.getRule().getDefinition();

		@BeforeEach
		void setUp() {
			lecture = new Lecture();
		}
		
		
		@Test
		@DisplayName("return false if lecture fields not set")
		void testInvalidLecture() {
			lecture.addStudent(new Teacher());
			String message = "Should return false";
			
			assertFalse(hasId.test(lecture), message);
			assertFalse(hasDate.test(lecture), message);
			assertFalse(hasCourse.test(lecture), message);
			assertFalse(hasTeacher.test(lecture), message);
			assertFalse(validTeacher.test(lecture), message);
			assertFalse(validStudents.test(lecture), message);
		}
		
		
		@Test
		@DisplayName("return true if lecture fields set")
		void testValidLecture() {
			lecture.setLectureId(1);
			lecture.setDate(LocalDateTime.now());
			lecture.setCourse(new Course());
			lecture.setTeacher(new Teacher());
			lecture.addStudent(new Student());
			String message = "Should return true";
			
			assertTrue(hasId.test(lecture), message);
			assertTrue(hasDate.test(lecture), message);
			assertTrue(hasCourse.test(lecture), message);
			assertTrue(hasTeacher.test(lecture), message);
			assertTrue(validTeacher.test(lecture), message);
			assertTrue(validStudents.test(lecture), message);
		}	
	}
	
	
	@Nested
	@DisplayName("Course rules")
	class CourseRulesTest {
		
		Course course;
		
		Predicate<Course> hasId = 
				CourseRules.HAS_ID.getRule().getDefinition();
		Predicate<Course> hasSubject = 
				CourseRules.HAS_SUBJECT.getRule().getDefinition();
		Predicate<Course> hasDescription = 
				CourseRules.HAS_DESCRIPTION.getRule().getDefinition();

		@BeforeEach
		void setUp() {
			course = new Course();
		}
		
		
		@Test
		@DisplayName("return false if course fields not set")
		void testInvalidCourse() {
			String message = "Should return false";
			
			assertFalse(hasId.test(course), message);
			assertFalse(hasSubject.test(course), message);
			assertFalse(hasDescription.test(course), message);
		}
		
		
		@Test
		@DisplayName("return true if course fields set")
		void testValidCourse() {
			course.setCourseId(1);
			course.setSubject("Beating horse");
			course.setDescription("Engaging in useless activities");
			String message = "Should return true";
			
			assertTrue(hasId.test(course), message);
			assertTrue(hasSubject.test(course), message);
			assertTrue(hasDescription.test(course), message);
		}
	}



}
