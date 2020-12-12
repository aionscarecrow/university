package ua.com.foxminded.university.service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.repository.MemberRepository;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.service.trackers.MemberTracker;
import ua.com.foxminded.university.service.validators.ConfigurableEntityValidator;
import ua.com.foxminded.university.service.validators.EntityValidator;
import ua.com.foxminded.university.service.validators.ValidationRules.MemberRules;

@Service
public class MemberServiceImpl implements MemberService, InitializingBean {
	
	@Autowired
	private MemberRepository repository;
	
	@Autowired
	private LectureService lectureService;
	
	@Autowired
	@Qualifier("memberValidator")
	private EntityValidator<Member> validator;
	
	@Autowired
	private MemberTracker typeTracker;
	
	private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		configureEntityValidator();
	}

	
	@Override
	public void create(Member member) throws ServiceException {
		validator.validateCreateable(member);
		log.debug("Creating member [{}]", member);
		
		try {
			repository.save(member);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to create member", e);
		}
		log.info("member [{}] created", member);
	}
	

	@Override
	public Member retrieveById(int id) throws ServiceException {
		validator.validateId(id);
		log.debug("Retrieving member by id [{}]", id);
		
		try {
			Member member = repository.findById(id).orElseThrow(
					() -> new ServiceException("Member not found"));
			typeTracker.trackType(member);
			
			return downCast.apply(member);
			
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
	}
	

	@Override
	public List<Member> retrieveAll() throws ServiceException {
		log.debug("retrieving all members");
		try {
			List<Member> members = new LinkedList<>(); 
			repository.findAllByOrderByMemberIdAsc().forEach(members::add);
			typeTracker.trackTypes(members);
			
			return downCastList.apply(members);
			
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to retrieve all members", e);
		}

	}
	

	@Override
	public void update(Member member) throws ServiceException {
		validator.validateUpdatable(member);
		
		if(typeTracker.typeIdChanged(member)) {
			validateChanges(member);
		}
		
		log.debug("Updating member with data [{}]", member);
		
		try {
			repository.save(member);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to update member", e);
		}
		log.info("Member updated with data [{}]", member);
	}
	

	private void validateChanges(Member member) throws ServiceException {
		if(getLectureCount(member) > 0) {
			log.error("Unacceptable state change for [{}]", member);
			
			throw new ServiceException(
					"Unacceptable state change. Member type cannot be altered - "
					+ "found scheduled lectures for id " + member.getMemberId());
		}
		log.debug("State change accepted. No scheduled lectures found");
	}
	
	
	@Override
	public Integer getLectureCount(Member member) throws ServiceException {
		try {
			return lectureService.getLectureCountFor(member);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to retrieve lecture count", e);
		}
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Member member) throws ServiceException {
		validator.validateDeletable(member);
			
		log.debug("Deleting member [{}]", member);
		
		try {
			removeFromLectures(member);
			repository.delete(upCast.apply(member));
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Failed to delete member", e);
		}
	}
	
	private void removeFromLectures(Member member) {
		for(Lecture lecture : member.getLectures()) {
			lecture.removeStudent(member);
		}
	}

	
	UnaryOperator<Member> upCast = Member::new;
			
	UnaryOperator<Member> downCast = m -> 
			(m.getTypeId() == Teacher.MEMBER_TYPE)? new Teacher(m) : new Student(m);
			
	UnaryOperator<List<Member>> downCastList = list -> 
			list.stream().map(downCast::apply).collect(Collectors.toList());
	
			
	private void configureEntityValidator() {
		log.info("Configuring validator for [{}]", Member.class);
		ConfigurableEntityValidator<Member> config = 
				(ConfigurableEntityValidator<Member>) this.validator;
		
		config.addDeletionRule(MemberRules.HAS_ID);
		config.addUpdateRule(MemberRules.HAS_ID);
		config.addUpdateRule(MemberRules.HAS_TYPE_ID);
		config.addUpdateRule(MemberRules.HAS_FIRST_NAME);
		config.addUpdateRule(MemberRules.HAS_LAST_NAME);
		config.addCreationRule(MemberRules.HAS_TYPE_ID);
		config.addCreationRule(MemberRules.HAS_FIRST_NAME);
		config.addCreationRule(MemberRules.HAS_LAST_NAME);
	}

}



