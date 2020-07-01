package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.MemberDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service")
class MemberServiceImplTest {
	
	@Mock
	MemberDao memberDao;
	
	MemberService memberService;
	Member student;
	List<Member> members;

	@BeforeEach
	void setUp() throws Exception {
		memberService = new MemberServiceImpl(memberDao);
	}
	
	@Nested
	@DisplayName("Create")
	class CreateTest {
		
		@Test
		@DisplayName("calls method to create member")
		void testCreate() throws ServiceException, DaoException {
			student = new Student("Biff", "Webster");
			memberService.create(student);
			verify(memberDao).create(student);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testCreateThrowsIfNull() {
			assertThrows(ServiceException.class, () -> memberService.create(null),
					"should throw ServiceException if argument is null");
		}

	}
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves member object by Id")
		void testRetrieveById() throws ServiceException {
			student = new Student("Phil", "Leotardo");
			when(memberDao.retrieveById(anyInt()))
							.thenReturn(student);
			
			Member actualMember = 
					memberService.retrieveById(ThreadLocalRandom.current().nextInt(2, 99));
			assertEquals(student, actualMember, "should return correct Member object");
		}
		
		@Test
		@DisplayName("throws if invalid id passed")
		void testRetrieveThrowsIfIdInvalid() {
			assertThrows(
					ServiceException.class, 
					() -> memberService.retrieveById(0),
					"should throw ServiceException if id invalid"
					);
		}

		@Test
		@DisplayName("retrieves list of members by lecture Id")
		void testRetrieveByLectureId() throws ServiceException {
			members = new ArrayList<>();
			when(memberDao.retrieveByLectureId(anyInt())).thenReturn(members);
			
			List<Member> actualMembers = memberService.retrieveByLectureId(1);
			
			assertArrayEquals(
					members.toArray(), 
					actualMembers.toArray(), 
					"should return correct member object"
				);
		}
		
		@Test
		@DisplayName("throws if lecture id invalid")
		void testRetrieveByLectureIdThrowsIfIdInvalid() {
			assertThrows(
					ServiceException.class,
					() -> memberService.retrieveByLectureId(0),
					"should throw ServiceException if course id field not set"
					);
		}

		@Test
		@DisplayName("retrieves all teacher objects")
		void testRetrieveAllTeachers() throws ServiceException {
			members = new ArrayList<>();
			when(memberDao.retrieveAllTeachers()).thenReturn(members);
			
			List<Member> actualMembers = memberService.retrieveAllTeachers();
			assertArrayEquals(members.toArray(), actualMembers.toArray());
		}
		
		@Test
		@DisplayName("retrieves all student objects")
		void testRetrieveAllStudents() throws ServiceException {
			members = new ArrayList<>();
			when(memberDao.retrieveAllStudents()).thenReturn(members);
			
			List<Member> actualMembers = memberService.retrieveAllStudents();
			assertArrayEquals(members.toArray(), actualMembers.toArray());
		}
		
		@Test
		@DisplayName("retrieves all member objects")
		void testRetrieveAll() throws ServiceException {
			members = new ArrayList<>();
			when(memberDao.retrieveAll()).thenReturn(members);
			
			List<Member> actualMembers = memberService.retrieveAll();
			assertArrayEquals(
					members.toArray(), 
					actualMembers.toArray(),
					"should return correct members list"
				);
		}
	}
	
	
	@Nested
	@DisplayName("Update")
	class UpdateTest {
		
		@Test
		@DisplayName("calls method to update member")
		void testUpdate() throws ServiceException, DaoException {
			student = new Student("Ronald", "McDonald");
			student.setMemberId(ThreadLocalRandom.current().nextInt(2, 99));
			memberService.update(student);
			verify(memberDao).update(student);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testUpdateThrowsIfNull() {
			assertThrows(ServiceException.class, () -> memberService.update(null),
					"should throw ServiceException if argument is null");
		}
		
		@Test
		@DisplayName("throws if member id invalid")
		void testUpdateThrowsIfIdInvalid() {
			student = new Student("Kyle", "Buttler");
			student.setMemberId(0);
			assertThrows(ServiceException.class, () -> memberService.update(student),
					"should throw ServiceException if course id field not set");
		}
	}

	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete member")
		void testDelete() throws ServiceException, DaoException {
			student = new Student("Ronald", "McDonald");
			student.setMemberId(ThreadLocalRandom.current().nextInt(2, 99));
			memberService.delete(student);
			verify(memberDao).delete(student);
		}
		
		@Test
		@DisplayName("throws if null passed")
		void testDeleteThrowsIfNull() {
			assertThrows(ServiceException.class, () -> memberService.delete(null),
					"should throw ServiceException if argument is null");
		}
		
		@Test
		@DisplayName("throws if member id invalid")
		void testDeleteThrowsIfIdInvalid() {
			student = new Student("Kyle", "Buttler");
			student.setMemberId(0);
			assertThrows(ServiceException.class, () -> memberService.delete(student),
					"should throw ServiceException if course id field not set");
		}
	}

	
}

