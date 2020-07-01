package ua.com.foxminded.university.dao.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.dao.LectureDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

public class LectureDaoImpl extends NamedParameterJdbcDaoSupport implements LectureDao {
	
	@Autowired
	private CourseDaoImpl courseDao;
	@Autowired
	private MemberDaoImpl memberDao;
	
	private static final Logger log = LoggerFactory.getLogger(LectureDaoImpl.class);
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Integer create(Lecture lecture) throws DaoException {
		log.debug("creating lecture [{}]", lecture.stringify());

		SimpleJdbcInsertOperations simpleInsert = 
				new SimpleJdbcInsert(getDataSource())
					.withTableName("lectures")
					.usingGeneratedKeyColumns("lecture_id");
				
		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("courseId", lecture.getCourse().getCourseId())
				.addValue("date", Timestamp.valueOf(lecture.getDate()))
				.addValue("teacherId", lecture.getTeacher().getMemberId());
		
		Number id = simpleInsert.executeAndReturnKey(params);
		log.debug("obtained generated lecture id [{}]", id);
		log.info("created lecture [{}]", lecture.stringify());
		
		return id.intValue();
	}
	

	@Override
	public Lecture retrieveById(int lectureId) {
		log.debug("retrieving lecture by id [{}]", lectureId);
		
		String query = "SELECT * FROM lectures WHERE lecture_id=?";
		return this.getJdbcTemplate().queryForObject(query, new LectureMapper(), lectureId);
	}
	
	
	@Override
	public List<Lecture> retrieveAll() {
		log.info("retrieving all lectures");
		
		String query = "SELECT * FROM lectures ORDER BY lecture_id";
		return this.getJdbcTemplate().query(query, new LectureMapper());
	}
	
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void update(Lecture lecture) throws DaoException {
		log.debug("updating lecture of id [{}] with data [{}]", 
				lecture.getLectureId(), lecture.stringify());
		
		String query = "UPDATE lectures SET course_id=?, date=?, teacher_id=? "
				+ "WHERE lecture_id=?";
		int rowsAffected = this.getJdbcTemplate().update(
						query, 
						lecture.getCourse().getCourseId(),
						Timestamp.valueOf(lecture.getDate()),
						lecture.getTeacher().getMemberId(),
						lecture.getLectureId()
		);
		
		if(rowsAffected == 0) {
			log.error("failed to update lecture");
			throw new DaoException("Failed to update lecture. 0 rows affected");
		}
		
		log.info("lecture of id [{}] updated with data [{}]", 
				lecture.getLectureId(), lecture.stringify());
	}
	
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void addStudent(Lecture lecture, Member member) throws DaoException {
		log.debug("adding student [{}] to lecture [{}]", 
					member.getMemberId(), lecture.getLectureId());
			
		String query = "INSERT INTO lecture_student VALUES(?, ?)";
		alterStudentBinding(query, lecture, member);
	}
	
	
	@Override
	public void removeStudent(Lecture lecture, Member member) throws DaoException {
		log.debug("removing student [{}] from lecture [{}]",
					member.getMemberId(), lecture.getLectureId());
		
		String query = "DELETE FROM lecture_student WHERE lecture_id=? AND student_id=?";
		alterStudentBinding(query, lecture, member);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void removeStudentBindings(Lecture lecture) throws DaoException {
		log.debug("removing all student bindings for lecture [{}]", lecture);
		
		String query = "DELETE FROM lecture_student WHERE lecture_id=?";
		alterStudentBinding(query, lecture, null);
	}
	
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void alterStudentBinding(String query, Lecture lecture, Member member) 
																	throws DaoException {
		if(member != null && !(member instanceof Student)) {
			throw new DaoException(
					"alter binding failed: passed member not an instance of Student");
		}
		
		Object[] params;
		int rowsAffected = 0;
		
		if(member != null) {
			params = new Object[] {lecture.getLectureId(), member.getMemberId()};
			log.debug("altering lecture-student [{}-{}] binding", 
					lecture.getLectureId(), member.getMemberId());
		} else {
			params = new Object[] {lecture.getLectureId()};
			log.debug("altering lecture-student binding for lecture id [{}]", 
					lecture.getLectureId());
		}

		rowsAffected = this.getJdbcTemplate().update(query, params);
	
		if(rowsAffected == 0) {
			log.warn("Altering student-lecture binding had no effect: 0 rows affected");
		}
	}

	
	@Override
	public void delete(Lecture lecture) throws DaoException {
		log.debug("deleting lecture [{}]", lecture.stringify());
		
		String query ="DELETE FROM lectures WHERE lecture_id=?";
		int rowsAffected = this.getJdbcTemplate().update(
				query, lecture.getLectureId()
				);
		
		if(rowsAffected == 0) {
			log.error("failed to delte lecture");
			throw new DaoException("Failed to delete lecture. 0 rows affected");
		}
		
		log.info("deleted lecture of id [{}]", lecture.getLectureId());
	}

	
	public final class LectureMapper implements RowMapper<Lecture>{
			@Override
			public Lecture mapRow(ResultSet rs, int rowNum) throws SQLException {
				log.trace("mapping row to lecture object");
				Lecture lecture = new Lecture();
				
				Integer lectureId = rs.getInt("lecture_id");
				log.trace("setting lecture id [{}]", lectureId);
				lecture.setLectureId(lectureId);
				
				LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
				log.trace("setting lecture date [{}]", date);
				lecture.setDate(date);
				
				Course course = courseDao.retrieveById(rs.getInt("course_id"));
				log.trace("setting lecture course [{}]", course);
				lecture.setCourse(course);
				
				List<Member> members = memberDao.retrieveByLectureId(lectureId);
				for(Member member : members) {
					if(member instanceof Student) {
						log.trace("adding student [{}] to lecture", member);
						lecture.addStudent((Student)member);
					} else if (member instanceof Teacher) {
						log.trace("adding teacher [{}] to lecture", member);
						lecture.setTeacher((Teacher)member);
					} else {
						log.warn("member type not recognized [{}]", member);
					}
				}
				return lecture;
			}
	}
}
