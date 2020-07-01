package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.LectureDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lecture Service")
class LectureServiceImplTest {
	
	@Mock
	LectureDao lectureDao;
	
	LectureService lectureService;
	Lecture lecture;


	@BeforeEach
	void setUp() throws Exception {
		lectureService = new LectureServiceImpl(lectureDao);
		lecture = new Lecture();
		lecture.setLectureId(999);
	}
	
	@Nested
	@DisplayName("Create")
	class CreateTest {
		
		@Test
		@DisplayName("calls creation method")
		void testCreate() throws ServiceException, DaoException {
			lectureService.create(lecture);
			verify(lectureDao).create(lecture);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testCreateThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.create(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("calls addStudent if students present")
		void testCreateAddsStudents() throws ServiceException, DaoException {
			when(lectureDao.create(any(Lecture.class))).thenReturn(1000);
			lecture.addStudent(new Student());
			
			lectureService.create(lecture);
			verify(lectureDao).addStudent(
					any(Lecture.class), any(Student.class)
				);
		}
		
	}
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves Lecture object by id")
		void testRetrieveById() throws ServiceException {
			when(lectureDao.retrieveById(anyInt())).thenReturn(lecture);
			
			Lecture actualLecture = lectureService.retrieveById(999);
			assertEquals(lecture, actualLecture, 
					"should return correct lecture object by id");
		}
		
		@Test
		@DisplayName("throws if invalid id passed")
		void testRetrieveThrowsIfIdInvalid() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.retrieveById(0),
					"should throw ServiceException if id invalid"
					);
		}
		
		@Test
		@DisplayName("retrieves list of lectures")
		void testRetrieveAll() throws ServiceException {
			List<Lecture> lectures = Collections.nCopies(2, lecture);
			when(lectureDao.retrieveAll()).thenReturn(lectures);
			
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
		void testUpdate() throws ServiceException, DaoException {
			lectureService.update(lecture);
			verify(lectureDao).update(lecture);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testUpdateThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.update(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testUpdateThrowsIfIdInvalid() {
			lecture.setLectureId(0);
			lecture.setDate(LocalDateTime.now());
			lecture.setCourse(new Course());
			lecture.setTeacher(new Teacher());
			assertThrows(
					ServiceException.class, 
					() -> lectureService.update(lecture),
					"should throw ServiceException if lecture id invalid"
					);
		}
		
		@Test
		@DisplayName("resets lecture-student binding")
		void testUpdateResetsBindings() throws ServiceException, DaoException {
			lectureService.update(lecture);
			verify(lectureDao).removeStudentBindings(any(Lecture.class));
		}
		
		@Test
		@DisplayName("adds students after binding reset")
		void testUpdateAddsStudents() throws ServiceException, DaoException {
			lecture.addStudent(new Student());
			lectureService.update(lecture);
			verify(lectureDao).addStudent(any(Lecture.class), any(Student.class));
			
		}
	}
	
		
	@Nested
	@DisplayName("AddStudent")
	class AddStudentTest {
		
		@Test
		@DisplayName("calls method to add student to lecture")
		void testAddStudent() throws ServiceException, DaoException {
			Student student = new Student();
			student.setMemberId(999);
			lectureService.addStudent(lecture, student);
			verify(lectureDao).addStudent(lecture, student);
		}
		
		@Test
		@DisplayName("throws if student null")
		void testAddStudentThrowsIfStudentNull() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.addStudent(lecture, null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture null")
		void testAddStudentThrowsIfLectureNull() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(1);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.addStudent(null, student),
					"should throw ServiceException if lecture argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testAddStudentThrowsIfLectureIdInvalid() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(1);
			lecture.setLectureId(0);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.addStudent(lecture, student),
					"should throw ServiceException if lecture id invalid"
					);
		}
		
		@Test
		@DisplayName("throws if student id invalid")
		void testAddStudentThrowsIfStudentIdInvalid() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(0);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.addStudent(lecture, student),
					"should throw ServiceException if member id invalid"
					);
		}
		
		@Test
		@DisplayName("throws if not instance of Student")
		void testAddStudentThrowsIfNotInstanceOfStudent() {
			Teacher student = new Teacher("Dexter", "Morgan");
			student.setMemberId(1);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.addStudent(lecture, student),
					"should throw ServiceException if argument not instance of Student"
					);
		}
	}
	
	@Nested
	@DisplayName("RemoveStudent")
	class RemoveTest {
		
		@Test
		@DisplayName("calls method to remove student from lecture")
		void testRemoveStudent() throws ServiceException, DaoException {
			Student student = new Student();
			student.setMemberId(999);
			lectureService.removeStudent(lecture, student);
			verify(lectureDao).removeStudent(lecture, student);
		}
		
		@Test
		@DisplayName("throws if student null")
		void testRemoveStudentThrowsIfStudentNull() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.removeStudent(lecture, null),
					"should throw ServiceException if student is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture null")
		void testRemoveStudentThrowsIfLectureNull() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(1);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.removeStudent(null, student),
					"should throw ServiceException if lecture is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testRemoveStudentThrowsIfLectureIdInvalid() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(1);
			lecture.setLectureId(0);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.removeStudent(lecture, student),
					"should throw ServiceException if lecture id invalid"
					);
		}
		
		@Test
		@DisplayName("throws if student id invalid")
		void testRemoveStudentThrowsIfStudentIdInvalid() {
			Student student = new Student("Dexter", "Morgan");
			student.setMemberId(0);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.removeStudent(lecture, student),
					"should throw ServiceException if member id invalid"
					);
		}
		
		@Test
		@DisplayName("throws if not instance of Student")
		void testRemoveStudentThrowsIfNotInstanceOfStudent() {
			Teacher student = new Teacher("Dexter", "Morgan");
			student.setMemberId(1);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.removeStudent(lecture, student),
					"should throw ServiceException if student not instance of Student"
					);
		}
	}
	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete lecture")
		void testDelete() throws ServiceException, DaoException {
			lectureService.delete(lecture);
			verify(lectureDao).delete(lecture);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testDeleteThrowsIfNull() {
			assertThrows(
					ServiceException.class, 
					() -> lectureService.delete(null),
					"should throw ServiceException if argument is null"
					);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testDeleteThrowsIfIdInvalid() {
			lecture.setLectureId(0);
			assertThrows(
					ServiceException.class, 
					() -> lectureService.delete(lecture),
					"should throw ServiceException if lecture id invalid"
					);
		}
	}

}
