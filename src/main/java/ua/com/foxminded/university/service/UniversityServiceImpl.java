package ua.com.foxminded.university.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.domain.University;
import ua.com.foxminded.university.domain.UniversityData;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.exceptions.ServiceException;

@Service
public class UniversityServiceImpl implements UniversityService {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private LectureService lectureService;
	
	private static final Logger log = LoggerFactory.getLogger(UniversityServiceImpl.class);

	@Override
	public University retrieveUniversity() throws ServiceException {
		log.info("Retrieving university");
		
		List<Member> members = retrieveAllMembers();
		List<Lecture> lectures = retrieveAllLectures();
		UniversityData data = buildUniversityData(members, lectures);
		
		return new University(data);
	}

	
	@Override
	public List<Lecture> retrieveMonthlySchedule(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("ailed to retrieve monthly schedule for member [{}]", member);
			throw new ServiceException("Member object null or member id field invalid");
		}
		
		log.debug("retrieving monthly schedule for member [{}]", member);
		
		List<Lecture> lectures = lectureService.retrieveMonthlyLectures();
				
		return filterByMember(lectures, member);
	}


	@Override
	public List<Lecture> retrieveDailySchedule(Member member) throws ServiceException {
		if(member == null || member.getMemberId() < 1) {
			log.error("failed to retrieve daily schedule for member [{}]", member);
			throw new ServiceException("Member object is null or member id field invalid");
		}
		
		log.debug("retrieving daily schedule for member [{}]", member);
		
		List<Lecture> lectures = lectureService.retrieveDailyLectures();
		
		return filterByMember(lectures, member);
	}
	
	
	private List<Lecture> filterByMember(List<Lecture> lectures, Member member){
		BiPredicate<Lecture, Member> hasMember = 
				(l, m) -> (l.getTeacher().equals(m) || l.getStudents().contains(m));
		return lectures.stream()
				.filter(l -> hasMember.test(l, member))
				.collect(Collectors.toList());
	}


	@Override
	public void addMember(Member member) throws ServiceException {
		if(member == null) {
			log.error("failed to add member [{}] to university", member);
			throw new ServiceException("Member object is null or member id field invalid");
		}
		
		log.debug("adding member [{}]", member);
		
		try {
			memberService.create(member);
		} catch(DataAccessException e) {
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
		
		if(log.isDebugEnabled())
			log.debug("scheduling lecture [{}]", lecture.stringify());
		
		try {
			lectureService.create(lecture);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
		if(log.isDebugEnabled())
			log.debug("lecture scheduled [{}]", lecture);
	}


	@Override
	public void cancelLecture(Lecture lecture) throws ServiceException {
		if(lecture == null || lecture.getLectureId() < 1) {
			log.error("failed to cancel lecture [{}]", lecture);
			throw new ServiceException("Lecture object is null or lecture id field invalid");
		}
		
		if(log.isDebugEnabled())
			log.debug("cancelling lecture [{}]", lecture.stringify());
		
		try {
			lectureService.delete(lecture);
		} catch(DataAccessException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		
		if(log.isDebugEnabled())
			log.debug("lecture cancelled [{}]", lecture);
	}
	
	
	private List<Member> retrieveAllMembers() throws ServiceException {
		return memberService.retrieveAll();
	}
	
	
	private List<Lecture> retrieveAllLectures() throws ServiceException {
		return lectureService.retrieveAll();
	}
	
	
	private UniversityData buildUniversityData(
			List<Member> allMembers, List<Lecture> lectures) {
		
		Map<Integer, List<Member>> splitMembers = splitMembersByType(allMembers);
		
		UniversityData data = new UniversityData();
		
		data.setLectures(lectures);

		data.setStudents(
				splitMembers.get(Student.MEMBER_TYPE)
				.stream()
				.map(Student.class::cast)
				.collect(Collectors.toList())
				);
		
		data.setTeachers(
				splitMembers.get(Teacher.MEMBER_TYPE)
				.stream()
				.map(Teacher.class::cast)
				.collect(Collectors.toList())
				);
		
		
		log.debug(
				"{} teachers, {} students and {} lectures added to UniversityData",
				data.getTeachers().size(), 
				data.getStudents().size(), 
				data.getLectures().size()
			);
		
		return data;
	}
	
	
	private Map<Integer, List<Member>> splitMembersByType(List<Member> members) {
		Map<Integer, List<Member>> result = 
				members.stream().collect(Collectors.groupingBy(Member::getTypeId));
		
		result.putIfAbsent(Teacher.MEMBER_TYPE, new LinkedList<Member>());
		result.putIfAbsent(Student.MEMBER_TYPE, new LinkedList<Member>());
		
		return result;
	}
}
