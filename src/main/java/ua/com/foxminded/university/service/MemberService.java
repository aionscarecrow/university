package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

public interface MemberService {

	void create(Member member) throws ServiceException;

	Member retrieveById(int id) throws ServiceException;

	List<Member> retrieveAll() throws ServiceException;

	void update(Member member) throws ServiceException;

	void delete(Member member) throws ServiceException;

	Integer getLectureCount(Member member) throws ServiceException;

}