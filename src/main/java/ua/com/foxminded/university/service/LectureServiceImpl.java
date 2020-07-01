package ua.com.foxminded.university.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.dao.LectureDao;
import ua.com.foxminded.university.dao.exceptions.DaoException;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@Service
public class LectureServiceImpl implements LectureService {
	
	private LectureDao lectureDao;
	private static final Logger log = LoggerFactory.getLogger(LectureServiceImpl.class);

	public LectureServiceImpl(@Autowired LectureDao lectureDao) {
		this.lectureDao = lectureDao;
	}
	
	@Override
	@Transactional
	public int create(final Lecture lecture) throws ServiceException {
		if (lecture == null) {
			log.error("Failed to create lecture [{}]", lecture);
			throw new ServiceException("passed Lecture object can't be null");
		}
		
		log.debug("creating lecture [{}]", lecture.stringify());
		
		try {
			int generatedId = lectureDao.create(lecture);
			if(!lecture.getStudents().isEmpty()) {
				
				Lecture targetLecture = new Lecture();
				targetLecture.setLectureId(generatedId);
				lecture.getStudents().forEach(targetLecture::addStudent);
				
				for(Student student : targetLecture.getStudents()) {
					lectureDao.addStudent(targetLecture, student);
				}
				log.debug("lecture created [{}]", lecture.stringify());
			}
			return generatedId;
			
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public Lecture retrieveById(int id) throws ServiceException {
		if (id < 1) {
			log.error("failed to retrieve lecture by id [{}]", id);
			throw new ServiceException("lecture id field invalid");
		}
		log.debug("retrieving lecture by id {}", id);
		
		try {
			return lectureDao.retrieveById(id);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public List<Lecture> retrieveAll() throws ServiceException {
		log.debug("retrieving all lectures");
		try {
			return lectureDao.retrieveAll();
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
	}
	
	@Override
	@Transactional
	public void update(Lecture lecture) throws ServiceException {
		
		if (!isValidLecture(lecture)) {
			log.error("failed to update lecture [{}]", lecture);
			throw new ServiceException("Lecture validation failed.");
		}
		log.debug("updating lecture with data [{}]", lecture.stringify());
		
		try {
			lectureDao.update(lecture);
			lectureDao.removeStudentBindings(lecture);
			if(!lecture.getStudents().isEmpty()) {
				for(Student student : lecture.getStudents()) {
					lectureDao.addStudent(lecture, student);
				}
			}
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("lecture updated {}", lecture.stringify());
	}
	
	@Override
	public void addStudent(Lecture lecture, Member member) 
										throws ServiceException {
		
		if(!isValidLecture(lecture) || !isValidStudent(member)) {
			log.error("failed to bind student [{}] to lecture [{}]", member, lecture);
			throw new ServiceException("failed to add student to lecture");
		}
		log.debug("adding student [{}] to lecture [{}]", member, lecture.stringify());
		
		try {
			lectureDao.addStudent(lecture, member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("student [{}] added to lecture [{}]", member, lecture);
	}
	
	@Override
	public void removeStudent(Lecture lecture, Member member) 
									throws ServiceException {
		
		if(!isValidLecture(lecture) || !isValidStudent(member)) {
			log.error("failed to remove student [{}] from lecture [{}]", member, lecture);
			throw new ServiceException("failed to remove student from lecture");
		}
		log.debug("removing student [{}] from lecture [{}]", member, lecture.stringify());
		
		try {
			lectureDao.removeStudent(lecture, member);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("student [{}] removed from lecture [{}]", member, lecture);
	}
	
	@Override
	public void delete(Lecture lecture) throws ServiceException {
		
		if(!isValidLecture(lecture)) {
			log.error("failed to delete lecture [{}]", lecture);
			throw new ServiceException("failed to delte lecture");
		}
		log.debug("deleting lecture [{}]", lecture.stringify());
		
		try {
			lectureDao.delete(lecture);
		} catch(DataAccessException | DaoException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		log.debug("lecture deleted [{}]", lecture);
	}
	
	private boolean isValidLecture(Lecture lecture) {
		if (lecture == null) {
			log.error("lecture validation failed [{}]", lecture);
			return false;
		} else if (lecture.getLectureId() < 1) {
			log.error("lecture id [{}] validation failed", lecture.getLectureId());
			return false;
		}
		log.debug("lecture validation successfull for [{}]", lecture.stringify());
		return true;
	}
	
	private boolean isValidStudent(Member member) {
		if (member == null) {
			log.error("student validation failed [{}]", member);
			return false;
		} else if (member.getMemberId() < 1) {
			log.error("student id [{}] validation failed", member.getMemberId());
			return false;
		} else if(!(member instanceof Student)) {
			log.error("student type [{}] validation failed", member.getClass());
			return false;
		}
		log.debug("student validation successful for [{}]", member);
		return true;
	}
	
	

}
