package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

public interface UniversityService {

	University retrieveUniversity() throws ServiceException;

	List<Lecture> retrieveMonthlySchedule(Member member) throws ServiceException;

	List<Lecture> retrieveDailySchedule(Member member) throws ServiceException;

	void addMember(Member member) throws ServiceException;

	void scheduleLecture(Lecture lecture) throws ServiceException;

	void cancelLecture(Lecture lecture) throws ServiceException;

}