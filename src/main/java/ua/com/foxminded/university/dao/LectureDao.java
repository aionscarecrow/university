package ua.com.foxminded.university.dao;

import java.util.List;

import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;

public interface LectureDao {

	Integer create(Lecture lecture) throws DaoException;

	Lecture retrieveById(int id);

	List<Lecture> retrieveAll();

	void update(Lecture lecture) throws DaoException;

	void addStudent(Lecture lecture, Member member) throws DaoException;
	
	void removeStudentBindings(Lecture lecture) throws DaoException;

	void removeStudent(Lecture lecture, Member member) throws DaoException;

	void delete(Lecture lecture) throws DaoException;

}