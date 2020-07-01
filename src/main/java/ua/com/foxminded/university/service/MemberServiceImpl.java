package ua.com.foxminded.university.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.MemberDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@Service
public class MemberServiceImpl implements MemberService {
	
	private MemberDao memberDao;
	private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

	public MemberServiceImpl(@Autowired MemberDao memberDao) {
		this.memberDao = memberDao;
	}
	
	
	@Override
	public void create(Member member) throws ServiceException {
		if(member == null) {
		  log.error("failed to create member [{}]", member); 
		  throw new ServiceException("Member object is null"); 
		}
		log.debug("creating member [{}]", member);
		
		try {
			memberDao.create(member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("member [{}] created", member);
	}
	

	@Override
	public Member retrieveById(int id) throws ServiceException {
		if(id < 1) {
			log.error("failed to retrieve member by id [{}]", id);
			throw new ServiceException("member id field invalid");
		}
		log.debug("retrieving member by id [{}]", id);
		
		try {
			return memberDao.retrieveById(id);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
	}
	

	@Override
	public List<Member> retrieveByLectureId(int lectureId) throws ServiceException {
		if(lectureId < 1) {
			log.error("failed to retrieve members by lecture id [{}]", lectureId);
			throw new ServiceException("lecture id field invalid");
		}
		log.debug("retrieving members by lecture id [{}]", lectureId);
		
		try {
			return memberDao.retrieveByLectureId(lectureId);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	

	@Override
	public List<Member> retrieveAllTeachers() throws ServiceException {
		log.debug("retrieving list of all teachers");
		try {
			return memberDao.retrieveAllTeachers();
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	

	@Override
	public List<Member> retrieveAllStudents() throws ServiceException {
		log.debug("retrieving list of all students");
		try {
			return memberDao.retrieveAllStudents();
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	

	@Override
	public List<Member> retrieveAll() throws ServiceException {
		log.debug("retrieving all members");
		try {
			return memberDao.retrieveAll();
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}

	}
	

	@Override
	public void update(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to update member [{}]", member);
			throw new ServiceException("Member object is null or id field invalid");
		}
		log.debug("updating member with data [{}]", member);
		
		try {
			memberDao.update(member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("member updated");
	}
	

	@Override
	public void delete(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to delete member [{}]", member);
			throw new ServiceException("Member object is null or id field invalid");
		}
		log.debug("deleting member [{}]", member);
		
		try {
			memberDao.delete(member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("member deleted");
	}
	
}


