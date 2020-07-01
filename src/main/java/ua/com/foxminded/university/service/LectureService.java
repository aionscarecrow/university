package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

public interface LectureService {

	int create(Lecture lecture) throws ServiceException;

	Lecture retrieveById(int id) throws ServiceException;

	List<Lecture> retrieveAll() throws ServiceException;

	void update(Lecture lecture) throws ServiceException;

	void addStudent(Lecture lecture, Member member) throws ServiceException;

	void removeStudent(Lecture lecture, Member member) throws ServiceException;

	void delete(Lecture lecture) throws ServiceException;

}