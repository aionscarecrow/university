package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.service.exceptions.ServiceException;

public interface CourseService {

	void create(Course course) throws ServiceException;

	Course retrieveById(int id) throws ServiceException;

	List<Course> retrieveAll() throws ServiceException;

	void update(Course course) throws ServiceException;

	void delete(Course course) throws ServiceException;

}