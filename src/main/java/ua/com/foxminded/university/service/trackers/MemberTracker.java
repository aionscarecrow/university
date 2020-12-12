package ua.com.foxminded.university.service.trackers;

import java.util.List;

import org.springframework.stereotype.Component;

import ua.com.foxminded.university.domain.entities.Member;

@Component
public class MemberTracker extends EntityStateTracker<Member, Integer> {
	
	private static final int CACHE_CAPACITY = 512;

	public MemberTracker() {
		super(CACHE_CAPACITY);
		this.setGetterFunctions(Member::getMemberId, Member::getTypeId);
	}
	

	public boolean typeIdChanged(Member member) {
		return trackedFieldChanged(member);
	}
	

	public void trackType(Member member) {
		track(member);
	}
	

	public void trackTypes(List<Member> members) {
		trackAll(members);
	}
}
