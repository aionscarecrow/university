package ua.com.foxminded.university.view.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.LectureService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.LecturePaginator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lecture Controller")
class LectureControllerTest {
	
	@Mock(name="lectureService")
	private LectureService lectureService;
	
	@Mock(name="courseController")
	private CourseController courseController;
	
	@Mock(name="memberController")
	private MemberController memberController;
	
	@Mock(name="lecturePaginator")
	private LecturePaginator<Lecture> pagination;
	
	@InjectMocks
	private LectureController lectureController = new LectureController();

	@Nested
	@DisplayName("List Lectures")
	class ListLecturesTest {
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			String expectedView = LectureController.TABLE_FRAGMENT;
			String actualView = 
					lectureController.listLectures(1, null).getViewName();
			
			assertEquals(expectedView, actualView);
		}
	}
	
	@Nested
	@DisplayName("Get lectures pageData")
	class GetLecturesPageDataTest {
		
		@Test
		@DisplayName("calls service with fetch param present")
		void testCallToService() throws ServiceException {
			lectureController.getLecturesPageData(1, "");
			
			verify(lectureService).retrieveAll();
		}
		
		
		@Test
		@DisplayName("doesn't call service with fetch=null if cache valid")
		void testNoCallToService() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			lectureController.getLecturesPageData(1, null);
			
			verify(lectureService, times(0)).retrieveAll();
		}
		
		
		@Test
		@DisplayName("calls service with fetch=null if cache invalid")
		void testCallServiceIfInvalidCache() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(false);
			lectureController.getLecturesPageData(1, null);
			
			verify(lectureService, times(1)).retrieveAll();
		}		
	}
	

	@Nested
	@DisplayName("Edit lecture")
	class EditLectureTest {
		
		@Test
		@DisplayName("requests lecture if id > 0")
		void testRequestLectureIfIdSet() throws ServiceException {
			lectureController.editLecture(1, Optional.of(1));
			
			verify(lectureService).retrieveById(anyInt());
		}
		
		
		@Test
		@DisplayName("does not request lecture if id == 0")
		void testNoLectureRequestIfNoIdSet() throws ServiceException {
			lectureController.editLecture(0, Optional.of(1));
			
			verify(lectureService, never()).retrieveById(anyInt());
		}
		
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			assertEquals(
					LectureController.EDIT_FRAGMENT,
					lectureController.editLecture(1, Optional.of(1)).getViewName(),
					"should return proper lecture editor view"
				);
			
		}
	}
	
	@Nested
	@DisplayName("Pick Member")
	class PickMemberTest {
		
		@Test
		@DisplayName("requests Teacher objects by member type 1")
		void testRequestsTeachers() throws ServiceException {
			lectureController.pickMember(1, 1);
			verify(memberController).getMembersPageData(1, null, Teacher.class);
		}
		
		
		@Test
		@DisplayName("returns proper view for member type 1")
		void testReturnsTeacherView() throws ServiceException {
			assertEquals(
					LectureController.TEACHER_PICKER,
					lectureController.pickMember(1, 1).getViewName()
				);
		}
		
		
		@Test
		@DisplayName("requests Student objects by member type 2")
		void testRequestsStudents() throws ServiceException {
			lectureController.pickMember(2, 1);
			verify(memberController).getMembersPageData(1, null, Student.class);
		}
		
		
		@Test
		@DisplayName("returns proper view for member type 2")
		void testReturnsStudentView() throws ServiceException {
			assertEquals(
					LectureController.STUDENT_PICKER,
					lectureController.pickMember(2, 1).getViewName()
				);
		}
		
		
	}
	

	@Nested
	@DisplayName("Pick Course")
	class PickCourseTest {
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			assertEquals(
					LectureController.COURSE_PICKER,
					lectureController.pickCourse(1).getViewName(),
					"should return proper course picker view"
				);
			
		}

	}
	
	
	@Nested
	@DisplayName("Pick date")
	class PickDateTest {
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			assertEquals(
					LectureController.DATE_PICKER,
					lectureController.pickDate(1, "2020-10-10T16:16:16").getViewName(),
					"should return proper date picker view"
				);
		}
		
		
		@Test
		@DisplayName("sets generated date to model if null date passed")
		void testGeneratesDateIfNull() throws ServiceException {
			Object dateTime = 
					lectureController.pickDate(1, null).getModelMap().get("dateTime"); 
			assertTrue(dateTime != null, "dateTime should be mapped to default");
			}
		

	}
	
	@Nested
	@DisplayName("Save lecture")
	class SaveLectureTest {
		
		Lecture lecture;
		
		@BeforeEach
		void setUp() {
			lecture = new Lecture();
		}
		
		
		@Test
		@DisplayName("calls update method for existent lecture")
		void testUpdatesExistent() throws ServiceException {
			lecture.setLectureId(1);
			lectureController.save(Optional.of(1), lecture);
			
			verify(lectureService).update(lecture);
		}
		
		
		@Test
		@DisplayName("calls create method for nonexistent lecture")
		void testCreatesNonexistent() throws ServiceException {
			when(lectureService.create(lecture)).thenReturn(lecture);
			lectureController.save(Optional.of(1), lecture);
			
			verify(lectureService).create(lecture);
		}
		
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			when(lectureService.create(lecture)).thenReturn(lecture);
			lectureController.save(Optional.of(1), lecture);
			
			verify(pagination).invalidateCache();
		}
		
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			when(lectureService.create(lecture)).thenReturn(lecture);
			lectureController.save(Optional.of(1), lecture);
			
			verify(pagination).invalidateCache();
		}
	}
	
	@Nested
	@DisplayName("Delete lecture")
	class deleteTest {
		
		Lecture lecture;
		
		@BeforeEach
		void setUp() {
			lecture = new Lecture();
			lecture.setLectureId(1);
		}
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String actualView =  
					lectureController.delete(
							lecture.getLectureId(), Optional.of(1)).getViewName();
			assertEquals(LectureController.TABLE_FRAGMENT, actualView, 
					"sould return proper lectures list view");
		}
		
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			lectureController.delete(lecture.getLectureId(), Optional.of(1));
			
			verify(pagination).invalidateCache();
		}
		
		
		@Test
		@DisplayName("calls delete method")
		void testCallsDelete() throws ServiceException {
			lectureController.delete(lecture.getLectureId(), Optional.of(1));
			
			verify(lectureService).delete(lecture);
		}
	}
	
}
