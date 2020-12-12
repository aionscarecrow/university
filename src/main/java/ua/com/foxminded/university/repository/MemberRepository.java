package ua.com.foxminded.university.repository;

import org.springframework.data.repository.CrudRepository;

import ua.com.foxminded.university.domain.entities.Member;

public interface MemberRepository extends CrudRepository<Member, Integer>{
	
	Iterable<Member> findAllByOrderByMemberIdAsc();

}
