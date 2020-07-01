package ua.com.foxminded.university.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@Service
public class CourseServiceImpl implements CourseService {

	private CourseDao courseDao;
	private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
	
	public CourseServiceImpl(@Autowired CourseDao courseDao) {
		this.courseDao = courseDao;
	}

	@Override
	public void create(Course course) throws ServiceException {
		if(course == null) {
			log.error("failed to create course [{}]", course);
			throw new ServiceException("passed Course object can't be null");
		}
		log.debug("creating course [{}]", course);
		
		try {
			courseDao.create(course);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("course created [{}]", course);
	}
	
	@Override
	public Course retrieveById(int id) throws ServiceException {
		if(id < 1) {
			log.error("failed to retrieve course by id [{}]", id);
			throw new ServiceException("invalid course id requested");
		}
		log.debug("retrieving course by id [{}]", id);
		
		try {
			return courseDao.retrieveById(id);
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
	}
	
	@Override
	public List<Course> retrieveAll() throws ServiceException {
		log.debug("retrieving all courses");
		try {
			return courseDao.retrieveAll();
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
	}
	
	@Override
	public void update(Course course) throws ServiceException {
		if(course == null || course.getCourseId() < 1) {
			log.error("failed to update course [{}]", course);
			throw new ServiceException("passed Course object is null or id field invalid");
		}
		log.debug("updating couse with data [{}]", course);
		
		try {
			courseDao.update(course);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("course updated with data [{}]", course);
	}

	@Override
	public void delete(Course course) throws ServiceException {
		if(course == null || course.getCourseId() < 1) {
			log.error("failed to delete course [{}]", course);
			throw new ServiceException("passed Course object is null or id field invalid");
		}
		log.debug("deleting course [{}]", course);
		
		try {
			courseDao.delete(course);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("course deleted [{}]", course);
	}
	
	

}
