package ua.com.foxminded.university.dao;

import java.util.List;

import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;

public interface CourseDao {

	void create(Course course) throws DaoException;

	Course retrieveById(int id);

	List<Course> retrieveAll();

	void update(Course course) throws DaoException;

	void delete(Course course) throws DaoException;

}