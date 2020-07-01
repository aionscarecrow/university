package ua.com.foxminded.university.dao;

import java.util.List;

import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;

public interface UniversityDao {

	University retrieveUniversity() throws DaoException;

	List<Lecture> retrieveMonthlySchedule(Member member);

	List<Lecture> retrieveDailySchedule(Member member);

	void addMember(Member member) throws DaoException;

	void scheduleLecture(Lecture lecture) throws DaoException;

	void cancelLecture(Lecture lecture) throws DaoException;
	
}