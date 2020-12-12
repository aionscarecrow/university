package ua.com.foxminded.university.view.controllers;

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
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.EntryQueryablePaginator;
import ua.com.foxminded.university.view.paginator.PageData;

@Controller
public class CourseController {
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	@Qualifier("coursePaginator")
	EntryQueryablePaginator<Course> pagination;
	
	public static final String TABLE_FRAGMENT = "fragments/courses :: coursesTable";
	public static final String EDIT_FRAGMENT = "fragments/courses :: courseForm";
	
	private static final Logger log = LoggerFactory.getLogger(CourseController.class);
	
	
	@GetMapping("/courses")
	public ModelAndView listCourses(@RequestParam(defaultValue="1") int page,
			@RequestParam(required = false) String fetch) throws ServiceException {
		
		log.debug("listCourses request params: page [{}], fetch [{}]", page, fetch);

		ModelAndView modelView = new ModelAndView();
		modelView.addObject("pageData", getCoursesPageData(page, fetch));
		modelView.setViewName(TABLE_FRAGMENT);
		
		return modelView;
	}
	
	
	PageData<Course> getCoursesPageData(int page, String fetch) throws ServiceException {
		if(fetch != null || !pagination.hasValidCache()) {
			pagination.setData(courseService.retrieveAll());
		}
		
		return pagination.getPageData(page);
	}
	
	
	Course getCourseById(int id) throws ServiceException {
		if(pagination.hasValidCache()) {
			Optional<Course> course = pagination.getEntry(id);
			if(course.isPresent()) {
				log.info("returning course from paginator [{}]", course.get());
				return course.get();
			}
		}
		log.info("returning course from service");
		return courseService.retrieveById(id);
	}
	
	
	@PostMapping("/deleteCourse")
	public ModelAndView delete(@RequestParam(required = true) int id,
			@RequestParam Optional<Integer> page) throws ServiceException {
		
		log.debug("delete request params: id [{}], page [{}]", id, page);
		
		Course course = getCourseById(id);
		
		courseService.delete(course);
		pagination.invalidateCache();
		
		return listCourses(page.orElse(1), "fetch");
	}
	
	
	@GetMapping("/courseForm")
	public ModelAndView getForm(@RequestParam(defaultValue = "0") int id,
			@RequestParam Optional<Integer> page) throws ServiceException {
		
		log.debug("form request params: id [{}], page [{}]", id, page);
		
		ModelAndView modelView = new ModelAndView();
		Course course;
		
		if(id > 0) {
			course = getCourseById(id);

			modelView.addObject("id", id);
			modelView.addObject("page", page.orElse(1));
		} else {
			course = new Course();
			
			modelView.addObject("page", Integer.MAX_VALUE);
		}
		modelView.addObject("url", "/saveCourse");
		modelView.addObject("course", course);
		modelView.setViewName(EDIT_FRAGMENT);
		
		return modelView;
	}
	
	
	@PostMapping("/saveCourse")
	public ModelAndView save(@RequestParam Optional<Integer> page,
			Course course) throws ServiceException {
		
		if(course.getCourseId() > 0){
			log.debug("update request params: [{}] , page [{}]", course, page);
			courseService.update(course);
		} else {
			log.debug("create request params: [{}] , page [{}]", course, page);
			courseService.create(course);
		}
		
		pagination.invalidateCache();
		return listCourses(page.orElse(Integer.MAX_VALUE), "fetch");
	}
}
