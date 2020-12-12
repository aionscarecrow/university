package ua.com.foxminded.university.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.repository.LectureRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.validators.ConfigurableEntityValidator;
import ua.com.foxminded.university.service.validators.EntityValidator;
import ua.com.foxminded.university.service.validators.ValidationRules.LectureRules;

@Service
public class LectureServiceImpl implements LectureService, InitializingBean {
	
	@Autowired
	private LectureRepository repository;
	
	@Autowired
	@Qualifier("lectureValidator")
	private EntityValidator<Lecture> validator;
	
	private static final Logger log = LoggerFactory.getLogger(LectureServiceImpl.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		configureEntityValidator();
	}
	
	
	@Override
	public Lecture create(final Lecture lecture) throws ServiceException {
		validator.validateCreateable(lecture);
		if(log.isDebugEnabled()) {
			log.debug("Creating lecture [{}]", lecture.stringify());
		}
		
		try {
			return repository.save(lecture);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to create Lecture", e);
		}
	}
	
	
	@Override
	public Lecture retrieveById(int id) throws ServiceException {
		validator.validateId(id);
		log.debug("Retrieving lecture by id {}", id);
		
		try {
			return repository.findById(id).orElseThrow(
					() -> new ServiceException("Lecture not found"));
		} catch(DataAccessException e) {
			log.error("Failed to retrieve lecture", e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	
	@Override
	public List<Lecture> retrieveAll() throws ServiceException {
		log.debug("Retrieving all lectures");
		
		try {
			List<Lecture> lectures = new LinkedList<>();
			repository.findAllByOrderByLectureIdAsc().forEach(lectures::add);
			return lectures;
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to retrieve all lectures", e);
		}
		
	}
	
	
	@Override
	public void update(Lecture lecture) throws ServiceException {
		validator.validateUpdatable(lecture);
		
		if(log.isDebugEnabled()) {
			log.debug("Updating lecture with data [{}]", lecture.stringify());
		}
		
		try {
			repository.save(lecture);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to update lecture", e);
		}
		
		if(log.isInfoEnabled()) {
			log.info("Lecture updated {}", lecture.stringify());
		}
	}

	
	@Override
	public void delete(Lecture lecture) throws ServiceException {
		
		validator.validateDeletable(lecture);
		
		if(log.isDebugEnabled()) {
			log.debug("Deleting lecture [{}]", lecture.stringify());
		}
		
		try {
			repository.delete(lecture);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<Lecture> retrieveMonthlyLectures() {
		
		LocalDateTime monthStart = LocalDateTime.now()
				.with(TemporalAdjusters.firstDayOfMonth())
				.truncatedTo(ChronoUnit.DAYS);
		
		LocalDateTime nextMonthStart = LocalDateTime.now()
				.with(TemporalAdjusters.lastDayOfMonth())
				.plusDays(1)
				.truncatedTo(ChronoUnit.DAYS);

		return retrieveByDateRange(monthStart, nextMonthStart);
	}
	
	@Override
	public List<Lecture> retrieveDailyLectures() {
		
		LocalDateTime currentDayStart = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
		LocalDateTime nextDayStart = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);

		return retrieveByDateRange(currentDayStart, nextDayStart);
	}
	
	private List<Lecture> retrieveByDateRange(LocalDateTime from, LocalDateTime to){
		List<Lecture> lectures = new LinkedList<>();
		repository.findByDateBetween(from, to).forEach(lectures::add);
		return lectures;
		
	}
	
	@Override
	public Integer getLectureCountFor(Member member) {
		return repository.countByStudentsMemberIdEquals(member.getMemberId());
	}
	

	private void configureEntityValidator() {
		log.info("Configuring validator for [{}]", Lecture.class);
		ConfigurableEntityValidator<Lecture> config = 
				(ConfigurableEntityValidator<Lecture>) this.validator;
		
		config.addCreationRule(LectureRules.HAS_DATE);
		config.addCreationRule(LectureRules.HAS_COURSE);
		config.addCreationRule(LectureRules.HAS_TEACHER);
		config.addUpdateRule(LectureRules.HAS_DATE);
		config.addUpdateRule(LectureRules.HAS_COURSE);
		config.addUpdateRule(LectureRules.HAS_TEACHER);
	}

}
