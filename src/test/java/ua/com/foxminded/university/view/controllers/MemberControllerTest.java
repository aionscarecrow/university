package ua.com.foxminded.university.view.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.MemberService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.EntryQueryablePaginator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Controller")
class MemberControllerTest {
	
	@Mock(name="memberService")
	private MemberService memberService;
	
	@Mock(name="memberPaginator")
	private EntryQueryablePaginator<Member> pagination;
	
	@InjectMocks
	private MemberController memberController = new MemberController();

	@Nested
	@DisplayName("List Members")
	class ListMembersTest {
		
		@Test
		@DisplayName("returns proper view")
		public void testReturnProperView() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			String expectedView = MemberController.TABLE_FRAGMENT;
			String actualView = 
					memberController.listMembers(1, null).getViewName();
			
			assertEquals(expectedView, actualView);
		}
		
	}
	
	@Nested
	@DisplayName("Get members page data")
	class getMembersPageDataTest {
		
		@Test
		@DisplayName("calls service with fetch param present")
		public void testCallToService() throws ServiceException {
			memberController.getMembersPageData(1, "");
			
			verify(memberService).retrieveAll();
		}
		
		
		@Test
		@DisplayName("doesn't call service with fetch=null if cache valid")
		public void testNoCallToService() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			memberController.getMembersPageData(1, null);
			
			verify(memberService, times(0)).retrieveAll();
		}
		
		
		@Test
		@DisplayName("calls service with fetch=null if cache invalid")
		public void testCallServiceIfInvalidCache() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(false);
			memberController.getMembersPageData(1, null);
			
			verify(memberService, times(1)).retrieveAll();
		}
	}
	
	@Nested
	@DisplayName("Get member by id")
	class GetMemberByIdTest {
		
		@Test
		@DisplayName("queries paginator for entry if cache valid")
		void testCallsPaginatorIfCacheValid() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			memberController.getMemberById(1);
			
			verify(pagination).getEntry(1);
		}
		
		
		@Test
		@DisplayName("calls service if cache invalid")
		void testCallsServiceIfCacheInvalid() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(false);
			memberController.getMemberById(1);
			
			verify(memberService).retrieveById(1);
		}
	}
	
	@Nested
	@DisplayName("Get form")
	class GetFormTest {
		
		Member member;
		
		@BeforeEach
		void setUp() {
			member = new Member();
			member.setMemberId(1);
		}
		
		@Test
		@DisplayName("requests member if id > 0")
		void testRequestLectureIfIdSet() throws ServiceException {
			when(pagination.hasValidCache()).thenReturn(true);
			memberController.getForm(member.getMemberId(), Optional.of(1));
			
			verify(pagination).getEntry(anyInt());
		}
		
		@Test
		@DisplayName("does not request member if id == 0")
		void testNoMemberRequestIfNoIdSet() throws ServiceException {
			member.setMemberId(0);
			memberController.getForm(member.getMemberId(), Optional.of(1));
			
			verify(pagination, never()).getEntry(anyInt());
		}
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = MemberController.EDIT_FRAGMENT;
			String actualView = memberController.getForm(
					member.getMemberId(), Optional.of(1)).getViewName();
			
			assertEquals(expectedView, actualView,
					"should return proper member form view");
		}
	}
	
	@Nested
	@DisplayName("Save member")
	class testSave {
		
		Member member;
		
		@BeforeEach
		void setUp() {
			member = new Member();
			member.setMemberId(1);
		}
		
		
		@Test
		@DisplayName("calls update method for existent member")
		void testUpdatesExistent() throws ServiceException {
			memberController.save(Optional.of(1), member);
			
			verify(memberService).update(member);
		}
		
		@Test
		@DisplayName("calls create method for nonexistent member")
		void testCreatesNonexistent() throws ServiceException {
			member.setMemberId(0);
			memberController.save(Optional.of(1), member);
			
			verify(memberService).create(member);
		}
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			memberController.save(Optional.of(1), member);
			
			verify(pagination).invalidateCache();
		}
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = MemberController.TABLE_FRAGMENT;
			String actualView = 
					memberController.save(Optional.of(1), member).getViewName();
			
			assertEquals(expectedView, actualView,
					"should return proper member list view");
		}
	}

	
	@Nested
	@DisplayName("Delete member")
	class deleteTest {
		
		Member member;
		
		@BeforeEach
		void setUp() {
			member = new Member();
			member.setMemberId(1);
		}
		
		@Test
		@DisplayName("returns proper view")
		void testReturnsProperView() throws ServiceException {
			String expectedView = MemberController.TABLE_FRAGMENT;
			String actualView =	memberController.
					delete(member.getMemberId(), Optional.of(1)).getViewName();
			
			assertEquals(expectedView, actualView, 
					"sould return proper member list view");
		}
		
		
		@Test
		@DisplayName("invalidates cache")
		void testInvalidatesCache() throws ServiceException {
			memberController.delete(member.getMemberId(), Optional.of(1));
			
			verify(pagination).invalidateCache();
		}
		
		
		@Test
		@DisplayName("calls delete method")
		void testCallsDelete() throws ServiceException {
			memberController.delete(member.getMemberId(), Optional.of(1));
			
			verify(memberService).delete(member);
		}
	}
}
