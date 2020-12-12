package ua.com.foxminded.university.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.repository.CourseRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.validators.ConfigurableEntityValidator;
import ua.com.foxminded.university.service.validators.EntityValidator;
import ua.com.foxminded.university.service.validators.ValidationRules.CourseRules;

@Service
public class CourseServiceImpl implements CourseService, InitializingBean {
	
	@Autowired
	private CourseRepository repository;
	
	@Autowired
	@Qualifier("courseValidator")
	private EntityValidator<Course> validator;
	
	private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		configureEntityValidator();
	}

	
	@Override
	public void create(Course course) throws ServiceException {
		validator.validateCreateable(course);
		
		log.debug("creating course [{}]", course);
		
		try {
			repository.save(course);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to create course", e);
		}
		log.debug("course created [{}]", course);
	}
	
	
	@Override
	public Course retrieveById(int id) throws ServiceException {
		validator.validateId(id);
		log.debug("retrieving course by id [{}]", id);
		
		try {
			return repository.findById(id).orElseThrow(
					() -> new ServiceException("Course not found"));
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to retrieve course", e);
		}
	}
	
	
	@Override
	public List<Course> retrieveAll() throws ServiceException {
		log.debug("retrieving all courses");
		try {
			List<Course> courses = new LinkedList<>();
			repository.findAllByOrderByCourseIdAsc().forEach(courses::add);
			return courses;
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to retrieve courses", e);
		}
	}
	
	
	@Override
	public void update(Course course) throws ServiceException {
		validator.validateUpdatable(course);
		
		log.debug("updating couse with data [{}]", course);
		
		try {
			repository.save(course);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to update course", e);
		}
		log.info("course updated with data [{}]", course);
	}

	
	@Override
	public void delete(Course course) throws ServiceException {
		validator.validateDeletable(course);
		
		log.debug("deleting course [{}]", course);
		
		try {
			repository.delete(course);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to delete course", e);
		}
	}
	
	
	private void configureEntityValidator() {
		log.info("Configuring validator for [{}]", Course.class);
		ConfigurableEntityValidator<Course> config = 
				(ConfigurableEntityValidator<Course>) this.validator;
		
		config.addDeletionRule(CourseRules.HAS_ID);
		config.addCreationRule(CourseRules.HAS_SUBJECT);
		config.addUpdateRule(CourseRules.HAS_ID);
		config.addUpdateRule(CourseRules.HAS_SUBJECT);
	}

}
