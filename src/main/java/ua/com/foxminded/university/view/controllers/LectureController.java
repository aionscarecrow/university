package ua.com.foxminded.university.view.controllers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.LectureService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.EntryQueryablePaginator;
import ua.com.foxminded.university.view.paginator.PageData;

@Controller
public class LectureController {

	@Autowired
	LectureService lectureService;
	
	@Autowired
	CourseController courseController;
	
	@Autowired
	MemberController memberController;

	@Autowired
	@Qualifier("lecturePaginator")
	EntryQueryablePaginator<Lecture> pagination;
	
	public static final String TABLE_FRAGMENT = "fragments/lectures :: lecturesTable";
	public static final String EDIT_FRAGMENT = "fragments/lectures :: lectureEditor";
	public static final String TEACHER_PICKER = 
			"fragments/lectures :: memberPicker(memberType='1')";
	public static final String STUDENT_PICKER = 
			"fragments/lectures :: memberPicker(memberType='2')";
	public static final String COURSE_PICKER = "fragments/lectures :: coursePicker";
	public static final String DATE_PICKER = "fragments/lectures :: datePicker";

	private static final Logger log = LoggerFactory.getLogger(LectureController.class);

	
	@GetMapping("/lectures")
	public ModelAndView listLectures(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String fetch) throws ServiceException {

		log.debug("listLectures request params: page [{}], fetch [{}]", page, fetch);

		ModelAndView modelView = new ModelAndView();
		modelView.addObject("pageData", getLecturesPageData(page, fetch));
		modelView.setViewName(TABLE_FRAGMENT);

		return modelView;
	}
	
	
	PageData<Lecture> getLecturesPageData(int page, String fetch) 
			throws ServiceException {
		if(fetch != null || !pagination.hasValidCache()) {
			pagination.setData(lectureService.retrieveAll());
		}
		
		return pagination.getPageData(page);
	}
	
	
	Lecture getLectureById(int id) throws ServiceException {
		if(pagination.hasValidCache()) {
			Optional<Lecture> lecture = pagination.getEntry(id);
			if(lecture.isPresent()) {
				log.info("returning lecture from paginator [{}]", 
						lecture.get().stringify());
				return lecture.get();
			}
		}
		log.info("returning lecture from service");
		return lectureService.retrieveById(id);
	}
	
	
	@GetMapping("/lectureEditor")
	public ModelAndView editLecture(@RequestParam(defaultValue = "0") int id,
			@RequestParam Optional<Integer> page) throws ServiceException {
		
		log.debug("lecture editor request params: id [{}], page [{}]", id, page);
		
		ModelAndView modelView = new ModelAndView(EDIT_FRAGMENT);
		if(id > 0) {
			modelView.addObject("lecture", getLectureById(id));
			log.info("existing lecture added to model");
		} else {
			Lecture lecture = new Lecture();
			lecture.setDate(LocalDateTime.now()
					.plusDays(1L).truncatedTo(ChronoUnit.MINUTES).withMinute(0));
			modelView.addObject("lecture", lecture);
			log.info("new lecture added to model");
		}
		
		modelView.addObject("url", "/saveLecture");
		modelView.addObject("page", page.orElse(1));
		modelView.addObject("id", id);
		
		return modelView;
	}
	
	
	@GetMapping("/memberPicker")
	public ModelAndView pickMember(
			@RequestParam(required = true) int memberType,
			@RequestParam(defaultValue = "1") int page)  throws ServiceException  {
		log.debug("Member picker request params: memberType [{}], page [{}]", 
				memberType, page);
			
		ModelAndView modelView = new ModelAndView(); 
		Class<?> memberClass = null;
		
		if(memberType == Teacher.MEMBER_TYPE) {
			memberClass = Teacher.class;
			modelView.setViewName(TEACHER_PICKER);
		} else if(memberType == Student.MEMBER_TYPE) {
			memberClass = Student.class;
			modelView.setViewName(STUDENT_PICKER);
		}
		log.debug("View set: [{}]", modelView.getViewName());
		
		PageData<Member> members = 
				memberController.getMembersPageData(page, null, memberClass);
		log.debug("Received members from memberController: [{}]", members);
		
		modelView.addObject("memberType", memberType);
		modelView.addObject("pageData", members);
		
		return modelView;
	}
	
	
	@GetMapping("/coursePicker")
	public ModelAndView pickCourse(
			@RequestParam(defaultValue = "1") int page) throws ServiceException {
		PageData<Course> courses = courseController.getCoursesPageData(page, null);
		log.debug("Received page of courses: [{}]", courses);
		
		ModelAndView modelView = new ModelAndView(COURSE_PICKER);
		modelView.addObject("pageData", courses);
		
		return modelView;
	}
	
	
	@GetMapping("/datePicker")
	public ModelAndView pickDate(@RequestParam(required = true) int id,
			@RequestParam(required = false) String date) {
		
		ModelAndView modelView = new ModelAndView(DATE_PICKER);

		if(date == null) {
			modelView.addObject("dateTime", 
					LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).withMinute(0));
		} else {
			modelView.addObject("dateTime", LocalDateTime.parse(date));
		}
		
		log.debug("DateTime set [{}]", modelView.getModelMap().get("dateTime"));

		return modelView;
	}
	
	
	@PostMapping("/saveLecture")
	public ModelAndView save(@RequestParam Optional<Integer> page,
			Lecture lecture) throws ServiceException {
		
		int lectureId;
		
		if(lecture.getLectureId() > 0) {
			log.debug("Updating existent lecture");
			lectureService.update(lecture);
			lectureId = lecture.getLectureId();
		} else {
			log.debug("Creating new lecture");
			lectureId = lectureService.create(lecture);
		}
		
		pagination.invalidateCache();
		
		ModelAndView modelView = new ModelAndView(EDIT_FRAGMENT);
		modelView.addObject("lecture", getLectureById(lectureId));
		modelView.addObject("url", "/saveLecture");
		modelView.addObject("page", page.orElse(1));
		
		return modelView;
	}

	
	@PostMapping("/deleteLecture")
	public ModelAndView delete(@RequestParam(required = true) int id,
			Optional<Integer> page) throws ServiceException {
		Lecture lecture = new Lecture();
		lecture.setLectureId(id);
		lectureService.delete(lecture);
		pagination.invalidateCache();
		return listLectures(page.orElse(1), null);
	}
	
}
