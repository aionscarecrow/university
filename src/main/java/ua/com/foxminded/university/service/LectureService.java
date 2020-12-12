package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

public interface LectureService {

	Lecture create(Lecture lecture) throws ServiceException;

	Lecture retrieveById(int id) throws ServiceException;

	List<Lecture> retrieveAll() throws ServiceException;

	void update(Lecture lecture) throws ServiceException;

	void delete(Lecture lecture) throws ServiceException;

	Integer getLectureCountFor(Member member);

	List<Lecture> retrieveMonthlyLectures();

	List<Lecture> retrieveDailyLectures();

}