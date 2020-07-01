package ua.com.foxminded.university.dao.db;


import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ua.com.foxminded.university.dao.MemberDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;


public class MemberDaoImpl extends JdbcDaoSupport implements MemberDao {
	
	private static final Logger log = LoggerFactory.getLogger(MemberDaoImpl.class);	
	
	@Override
	public void create(Member member) throws DaoException {
		
		log.debug("creating member [{}]", member);
		
		String query = "INSERT INTO members VALUES(DEFAULT, ?, ?, ?)";
		int rowsAffected = this.getJdbcTemplate().update(
						query, 
						member.getTypeId(),
						member.getFirstName(),
						member.getLastName()
		);
		
		if(rowsAffected == 0) {
			log.error("Member creation failed");
			throw new DaoException("Failed to create member. 0 rows affected");
		}
		
		log.info("member created [{}]", member);
		
	}

	
	@Override
	public Member retrieveById(int id) {
		log.debug("retrieving member by id [{}]", id);
		
		String query = "SELECT * FROM members WHERE member_id=?";
		Member member = this.getJdbcTemplate().queryForObject(
				query, BeanPropertyRowMapper.newInstance(Member.class), id
			);
		return downCast.apply(member);
	}
	
	
	@Override
	public List<Member> retrieveByLectureId(int lectureId) {
		log.debug("retrieving members by lecture id [{}]", lectureId);
		
		String query = 
				"SELECT DISTINCT m.* FROM members m JOIN (SELECT l.*, ls.student_id " +
				"FROM lecture_student ls RIGHT JOIN lectures l ON" +
				"(ls.lecture_id=l.lecture_id)) AS sub ON(m.member_id=teacher_id OR " +
				"m.member_id=student_id) WHERE lecture_id=?";
		List<Member> members = this.getJdbcTemplate().query(
				query, BeanPropertyRowMapper.newInstance(Member.class), lectureId
			);
		
		return downCastList.apply(members);
	}
	
	
	@Override
	public List<Member> retrieveAllTeachers() {
		return retrieveAllOfType(1);
	}
	
	
	@Override
	public List<Member> retrieveAllStudents() {
		return retrieveAllOfType(2);
	}
	
	@Override
	public List<Member> retrieveAll() {
		return retrieveAllOfType();
	}
	
	
	@Override
	public void update(Member member) throws DaoException {
		log.debug("updating member of id [{}] with data [{}]", 
				member.getMemberId(), member);
		
		String query = "UPDATE members SET type_id=?, first_name=?, " +
				"last_name=? WHERE member_id=?";
		int rowsAffected = this.getJdbcTemplate().update(
						query, 
						member.getTypeId(),
						member.getFirstName(),
						member.getLastName(),
						member.getMemberId()
		);
		
		if(rowsAffected == 0) {
			log.error("failed to update member");
			throw new DaoException("Failed to update member. 0 rows affected");
		}
		
		log.info("member updated [{}]", member);
	}
	
	
	@Override
	public void delete(Member member) throws DaoException {
		log.debug("deleting member of id [{}]", member.getMemberId());
		
		String query  = "DELETE FROM members WHERE member_id=?";
		int rowsAffected = this.getJdbcTemplate().update(
				query, member.getMemberId()
				);
		
		if(rowsAffected == 0) {
			log.error("failed to delete member of id [{}]", member.getMemberId());
			throw new DaoException("Failed to delete member. 0 rows affected");
		}
		
		log.info("member of id [{}] deleted", member.getMemberId());
	}
	
	
	private List<Member> retrieveAllOfType(int... type) {
		log.debug("retrieving all members by type [{}]", type);
		
		String query;
		Object[] queryArgs = new Object[type.length];
		
		if(type.length == 0) {
			query = "SELECT * FROM members ORDER BY member_id";
			log.debug("selecting query [{}]", query);
		} else {
			query = "SELECT * FROM members WHERE type_id=? ORDER BY member_id";
			log.debug("selecting query [{}]", query);
			queryArgs[0] = type[0];
		}
		
		List<Member> members = this.getJdbcTemplate().query(
				query, BeanPropertyRowMapper.newInstance(Member.class), queryArgs
			);
		
		log.info("returning {} members of type [{}]", members.size(), type);
		return downCastList.apply(members);
	}
	
	
	private UnaryOperator<List<Member>> downCastList = list -> 
			list.stream()
			.map(m -> (m.getTypeId() == 1)? new Teacher(m) : new Student(m))
			.collect(Collectors.toList());

			
	private UnaryOperator<Member> downCast = m -> 
			(m.getTypeId() == 1)? new Teacher(m) : new Student(m);

}
