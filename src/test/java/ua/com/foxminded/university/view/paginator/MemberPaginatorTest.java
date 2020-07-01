package ua.com.foxminded.university.view.paginator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ua.com.foxminded.university.domain.entities.Member;

@DisplayName("Member paginator")
class MemberPaginatorTest {
	
	EntryQueryablePaginator<Member> paginator;
	List<Member> members;

	@BeforeEach
	void setUp() {
		paginator = new MemberPaginator<>();
		members = new LinkedList<>();
		for(int i = 0; i < 5; i++) {
			Member member = new Member();
			member.setMemberId(i + 1);
			members.add(member);
		}
		paginator.setData(members);
	}

	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4, 5})
	@DisplayName("Queries entry by id")
	void testGetEntry(int expectedId) {
		int actualId = paginator.getEntry(expectedId).get().getMemberId();
		assertEquals(expectedId, actualId, 
				"requested id and id of obtained entry should match");
	}
	
	
	@Test
	@DisplayName("Returns first found entry")
	void testGetsFirstFound() {
		int id = members.size();
		Member addedMember = new Member();
		addedMember.setMemberId(id);
		members.add(addedMember);
		paginator.setData(members);
		
		Member firstFoundMember = paginator.getEntry(id).get();
		assertFalse(addedMember == firstFoundMember,
				"should not be the same member");
	}
	
	
	@Test
	@DisplayName("Returns empty Optional if no entry found")
	void testIfNotFound() {
		Optional<Member> member = paginator.getEntry(Integer.MAX_VALUE);
		assertFalse(member.isPresent());
	}

}
