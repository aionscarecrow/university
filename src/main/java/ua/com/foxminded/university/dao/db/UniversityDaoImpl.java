package ua.com.foxminded.university.dao.db;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import ua.com.foxminded.university.dao.UniversityDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.UniversityData;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

public class UniversityDaoImpl extends NamedParameterJdbcDaoSupport implements UniversityDao {
	
	@Autowired
	private MemberDaoImpl memberDao;
	@Autowired
	private LectureDaoImpl lectureDao;
	
	private static final Logger log = LoggerFactory.getLogger(UniversityDaoImpl.class);
	
	@Override
	public void addMember(Member member) throws DaoException {
		log.info("creating member");
		memberDao.create(member); 
	}
	
	@Override
	public void scheduleLecture(Lecture lecture) throws DaoException {
		log.info("scheduling lecture");
		lectureDao.create(lecture);
	}
	
	@Override
	public void cancelLecture(Lecture lecture) throws DaoException {
		log.info("cancelling lecture");
		lectureDao.delete(lecture);
	}

	@Override
	public University retrieveUniversity() throws DaoException {
		log.trace("creating University object");
		University university = new University();
		
		log.info("retrieving all students");
		List<Student> students = retrieveAllStudents()
									.stream()
									.map(Student.class::cast)
									.collect(Collectors.toList());
		
		log.info("retrieving all teachers");
		List<Teacher> teachers = retrieveAllTeachers().stream()
									.map(Teacher.class::cast)
									.collect(Collectors.toList());
		
		log.info("retrieving all lectures");
		List<Lecture> lectures = retrieveAllLectures();
		
		log.debug("setting {} teachers, {} students and {} lectures to UniversityData",
				teachers.size(), students.size(), lectures.size());
		UniversityData data = new UniversityData();
		data.setLectures(lectures);
		data.setStudents(students);
		data.setTeachers(teachers);
		
		log.info("setting UniversityData to university");
		university.setData(data);
		
		log.trace("returning University object");
		return university;
	}
	
	@Override
	public List<Lecture> retrieveMonthlySchedule(Member member) {
		log.debug("retrieving monthly schedule for member [{}]", member);
		
		String query = 
				"SELECT DISTINCT l.* FROM lectures l LEFT JOIN lecture_student ls ON" + 
				"(l.lecture_id=ls.lecture_id) WHERE(student_id=:id OR teacher_id=:id) AND " +
				"(EXTRACT(MONTH FROM l.date))=:month AND (EXTRACT(YEAR FROM l.date))=:year " +
				"ORDER BY l.date";
		
		return retrieveFilteredSchedule(member, query);
	}
	
	@Override
	public List<Lecture> retrieveDailySchedule(Member member) {
		log.debug("retrieving daily schedule for member [{}]", member);
		
		String query = 
				"SELECT DISTINCT l.* FROM lectures l LEFT JOIN lecture_student ls ON" + 
				"(l.lecture_id=ls.lecture_id) WHERE(student_id=:id OR teacher_id=:id) AND " +
				"(EXTRACT(DOY FROM l.date))=:day AND (EXTRACT(YEAR FROM l.date))=:year " +
				"ORDER BY l.date";
		
		return retrieveFilteredSchedule(member, query);
	}
	
	private List<Lecture> retrieveFilteredSchedule(Member member, String query) {
		
		LocalDateTime now = LocalDateTime.now();
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		
		Map<String, Integer> params = new HashMap<>();
		params.put("id", member.getMemberId());
		params.put("day", now.getDayOfYear());
		params.put("week", now.get(weekFields.weekOfWeekBasedYear()));
		params.put("month", now.getMonth().getValue());
		params.put("year", now.getYear());
		
		log.debug("using named parameters [{}]", params);
		
		List<Lecture> schedule = this.getNamedParameterJdbcTemplate().query(
				query, params, lectureDao.new LectureMapper()
				);
		
		log.info("returning list of {} lectures", schedule.size());
		return schedule;
	}
	
	private List<Member> retrieveAllStudents() {
		log.info("retrieving all students for university");
		return memberDao.retrieveAllStudents();
	}
	
	private List<Member> retrieveAllTeachers() {
		log.info("retrieving all teachers for university");
		return memberDao.retrieveAllTeachers();
	}
	
	private List<Lecture> retrieveAllLectures(){
		log.info("retrieving all lectures for university");
		return lectureDao.retrieveAll();
	}
}
