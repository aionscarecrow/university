package ua.com.foxminded.university.view.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	
	public static final String INDEX_VIEW = "index";
	
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	CourseController courseController;
	
	@Autowired
	MemberController memberController;
	
	@Autowired
	LectureController lectureController;
	
	@GetMapping("/")
	public String getIndexPage(HttpServletRequest request) {
		log.info("Index page requested from [{}]", request.getRemoteAddr());
		return INDEX_VIEW;
	}
}
