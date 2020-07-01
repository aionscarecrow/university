package ua.com.foxminded.university.dao;

import java.util.List;

import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Member;

public interface MemberDao {

	void create(Member member) throws DaoException;

	Member retrieveById(int id);

	List<Member> retrieveByLectureId(int lectureId);

	List<Member> retrieveAllTeachers();

	List<Member> retrieveAllStudents();
	
	List<Member> retrieveAll();

	void update(Member member) throws DaoException;

	void delete(Member member) throws DaoException;

}