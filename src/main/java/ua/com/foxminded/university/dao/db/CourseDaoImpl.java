package ua.com.foxminded.university.dao.db;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;

public class CourseDaoImpl extends JdbcDaoSupport implements CourseDao {
	
	private static final Logger log = LoggerFactory.getLogger("CourseDaoImpl.class");
	
	@Override
	public void create(Course course) throws DaoException {
		log.debug("creating course [{}]", course);
		
		String query = "INSERT INTO courses VALUES(DEFAULT, ?, ?)";
		int rowsAffected = this.getJdbcTemplate().update(
						query,
						course.getSubject(),
						course.getDescription()
		);
		
		if(rowsAffected == 0) {
			log.error("failed to create course");
			throw new DaoException("Failed to create course. 0 rows affected");
		}
		
		log.info("created course [{}]", course);
	}
	
	
	@Override
	public Course retrieveById(int courseId) {
		log.info("retrieving course by id [{}]", courseId);
		String query = "SELECT * FROM courses WHERE course_id=?";
		return this.getJdbcTemplate().queryForObject(
				query, BeanPropertyRowMapper.newInstance(Course.class), courseId
			);	
	}
	
	
	@Override
	public List<Course> retrieveAll() {
		log.info("retrieving all courses");
		
		String query = "SELECT * FROM courses ORDER BY course_id";
		return this.getJdbcTemplate().query(
				query, BeanPropertyRowMapper.newInstance(Course.class)
			);
	}
	
	
	@Override
	public void update(Course course) throws DaoException {
		log.debug("updating course of id [{}] with data [{}]", 
					course.getCourseId(), course);
		
		String query = "UPDATE courses SET subject=?, description=?"
				+ "WHERE course_id=?";
		int rowsAffected = this.getJdbcTemplate().update(
						query,
						course.getSubject(),
						course.getDescription(),
						course.getCourseId()
		);
		
		if(rowsAffected == 0) {
			log.error("failed to update course");
			throw new DaoException("Failed to update course. 0 rows affected");
		}
		
		log.info("course of id [{}] updated with data [{}]", 
					course.getCourseId(), course);
	}
	
	
	@Override
	public void delete(Course course) throws DaoException {
		log.debug("deleting course of id [{}]", course.getCourseId());
		
		String query ="DELETE FROM courses WHERE course_id=?";
		int rowsAffected = this.getJdbcTemplate().update(query, course.getCourseId());
		
		if(rowsAffected == 0) {
			log.error("failed to delete course");
			throw new DaoException("Failed to delete course. 0 rows affected");
		}
		
		log.info("course of id [{}] deleted", course.getCourseId());
		
	}
	
	
	

}
