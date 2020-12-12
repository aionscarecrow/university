package ua.com.foxminded.university.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.repository.MemberRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.trackers.MemberTracker;
import ua.com.foxminded.university.service.validators.EntityValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service")
class MemberServiceImplTest {
	
	@Mock
	MemberRepository repository;
	
	@Mock
	LectureService lectureService;
	
	@Mock(name="memberValidator")
	EntityValidator<Member> validator;
	
	@Mock(name="memberTracker")
	MemberTracker typeTracker;
	
	@InjectMocks
	MemberService memberService = new MemberServiceImpl();
	
	Member student;
	List<Member> members;

	@BeforeEach
	void setUp() throws Exception {
		
	}
	
	@Nested
	@DisplayName("Create")
	class CreateTest {
		
		@Test
		@DisplayName("calls method to create member")
		void testCreate() throws ServiceException {
			student = new Student("Biff", "Webster");
			memberService.create(student);
			verify(repository).save(student);
		}
		
		
		@Test
		@DisplayName("calls for proper object validation ")
		void testCallsForValidation() throws ServiceException {
			memberService.create(new Member());
			verify(validator).validateCreateable(any(Member.class));
		}
	}
	
	
	@Nested
	@DisplayName("Retrieve")
	class RetrieveTest {
		
		@Test
		@DisplayName("retrieves member object by Id")
		void testRetrieveById() throws ServiceException {
			student = new Student("Phil", "Leotardo");
			when(repository.findById(anyInt())).thenReturn(Optional.of(student));
			
			Member actualMember = 
					memberService.retrieveById(ThreadLocalRandom.current().nextInt(2, 99));
			assertEquals(student, actualMember, "should return correct Member object");
		}
		
		
		@Test
		@DisplayName("calls for proper id validation ")
		void testCallsForIdValidation() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(new Member()));
			memberService.retrieveById(1);
			verify(validator).validateId(anyInt());
		}
		
		
		@Test
		@DisplayName("calls to track entity")
		void testCallsToTrackEntity() throws ServiceException {
			when(repository.findById(anyInt())).thenReturn(Optional.of(new Member()));
			memberService.retrieveById(1);
			
			verify(typeTracker).trackType(any(Member.class));
		}
		
		@Test
		@DisplayName("throws if null retrieved")
		void testThrowsUponNull() {
			assertThrows(
					ServiceException.class, 
					() -> memberService.retrieveById(99),
					"should throw if null retrieved");
		}
	}
	
	
	@Nested
	@DisplayName("RetrieveAll")
	class RetrieveAllTest {
		
		@Test
		@DisplayName("retrieves all member objects")
		void testRetrieveAll() throws ServiceException {
			members = new ArrayList<>();
			
			List<Member> actualMembers = memberService.retrieveAll();
			assertArrayEquals(
					members.toArray(), 
					actualMembers.toArray(),
					"should return correct members list"
				);
		}
		
		
		@Test
		@DisplayName("calls to track multiple entities")
		void testCallsToTrackEntities() throws ServiceException {
			List<Member> members = new LinkedList<>();
			memberService.retrieveAll();
			
			verify(typeTracker).trackTypes(members);
		}
	}
	
	
	@Nested
	@DisplayName("Get lecture count")
	class LectureCountTest {
		
		@Test
		@DisplayName("returns proper count value")
		void testReturnsCount() throws ServiceException {
			int count = ThreadLocalRandom.current().nextInt(1, 99);
			Member member = new Member();
			member.setMemberId(1);
			when(lectureService.getLectureCountFor(member)).thenReturn(count);
			
			int expected = count;
			int actual = memberService.getLectureCount(member);
			assertEquals(expected, actual);
		}
	}
	
	
	@Nested
	@DisplayName("Update")
	class UpdateTest {
		
		@Test
		@DisplayName("calls method to update member")
		void testUpdate() throws ServiceException {
			student = new Student("Ronald", "McDonald");
			student.setMemberId(ThreadLocalRandom.current().nextInt(2, 99));
			memberService.update(student);
			verify(repository).save(student);
		}
		
		
		@Test
		@DisplayName("calls for proper object validation ")
		void testCallsForValidation() throws ServiceException {
			memberService.update(new Member());
			verify(validator).validateUpdatable(any(Member.class));
		}
		
		
		@Test
		@DisplayName("calls for changes validation if tracker reports them")
		void testGetsLectureCountIfStateChanged() throws ServiceException {
			when(typeTracker.typeIdChanged(any(Member.class))).thenReturn(true);
			memberService.update(new Member());
			verify(lectureService).getLectureCountFor(new Member());
		}
		
		
		@Test
		@DisplayName("throws if improper state change detected")
		void testThrowsIfStateChangeImproper() throws ServiceException {
			when(typeTracker.typeIdChanged(any(Member.class))).thenReturn(true);
			when(lectureService.getLectureCountFor(new Member())).thenReturn(1);

			assertThrows(
					ServiceException.class, 
					() -> memberService.update(new Member()),
					"Should throw ServiceException"
				);
		}
	}

	
	@Nested
	@DisplayName("Delete")
	class DeleteTest {
		
		@Test
		@DisplayName("calls method to delete member")
		void testDelete() throws ServiceException {
			student = new Member("Ronald", "McDonald");
			student.setMemberId(ThreadLocalRandom.current().nextInt(2, 99));
			memberService.delete(student);
			verify(repository).delete(student);
		}
		
		
		@Test
		@DisplayName("calls for proper object validation ")
		void testCallsForValidation() throws ServiceException {
			memberService.delete(new Member());
			verify(validator).validateDeletable(any(Member.class));
		}
	}
	
}

