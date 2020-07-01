package ua.com.foxminded.university.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.UniversityDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@Service
public class UniversityServiceImpl implements UniversityService {
	
	private UniversityDao universityDao;
	private static final Logger log = LoggerFactory.getLogger(UniversityServiceImpl.class);
	
	public UniversityServiceImpl(@Autowired UniversityDao universityDao) {
		this.universityDao = universityDao;
	}

	
	@Override
	public University retrieveUniversity() throws ServiceException {
		log.debug("retrieving university");
		try {
			return universityDao.retrieveUniversity();
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	
	@Override
	public List<Lecture> retrieveMonthlySchedule(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to retrieve monthly schedule for member [{}]", member);
			throw new ServiceException("Member object null or member id field invalid");
		}
		log.debug("retrieving monthly schedule for member [{}]", member);
		
		try {
			return universityDao.retrieveMonthlySchedule(member);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}


	@Override
	public List<Lecture> retrieveDailySchedule(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to retrieve daily schedule for member [{}]", member);
			throw new ServiceException("Member object is null or member id field invalid");
		}
		log.debug("retrieving daily schedule for member [{}]", member);
		
		try {
			return universityDao.retrieveDailySchedule(member);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}


	@Override
	public void addMember(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to add member [{}] to lecture", member);
			throw new ServiceException("Member object is null or member id field invalid");
		}
		log.debug("adding member [{}]", member);
		
		try {
			universityDao.addMember(member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("member added [{}]", member);
	}


	@Override
	public void scheduleLecture(Lecture lecture) throws ServiceException {
		if(lecture == null) {
			log.error("failed to schedule lecture [{}]", lecture);
			throw new ServiceException("Lecture object is null");
		}
		log.debug("scheduling lecture [{}]", lecture.stringify());
		
		try {
			universityDao.scheduleLecture(lecture);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("lecture scheduled [{}]", lecture);
	}


	@Override
	public void cancelLecture(Lecture lecture) throws ServiceException {
		if(lecture == null || lecture.getLectureId() < 1) {
			log.error("failed to cancel lecture [{}]", lecture);
			throw new ServiceException("Lecture object is null or lecture id field invalid");
		}
		log.debug("cancelling lecture [{}]", lecture.stringify());
		
		try {
			universityDao.cancelLecture(lecture);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("lecture cancelled [{}]", lecture);
	}
}
