package ua.com.foxminded.university.view.controllers.advice;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import ua.com.foxminded.university.service.exceptions.ServiceException;

@ControllerAdvice("ua.com.foxminded.university.view.controllers")
public class ControllerExceptionHandler {
	
	public static final String SERVICE_EXCEPTION_VIEW = "fragments/exceptions :: exceptionAlert";
	public static final String GENERIC_EXCEPTION_VIEW = "fragments/exceptions :: genericException";
	
	private static Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
	
	@ExceptionHandler(ServiceException.class)
	public ModelAndView handleServiceException(ServiceException e) {
		log.debug("Handling ServiceException - [{}]", e.getMessage());
		log.error(e.getMessage(), e);
		return prepareModel(e, SERVICE_EXCEPTION_VIEW);
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception e) {
		log.debug("Handling {} - [{}]",	e.getClass(), e.getMessage());
		log.error(e.getMessage(), e);
		return prepareModel(e, GENERIC_EXCEPTION_VIEW);
	}
	
	private ModelAndView prepareModel(Exception e, String view) {
		ModelAndView modelView = new ModelAndView(view);
		modelView.addObject("title", e.getClass().getSimpleName());
		modelView.addObject("message", e.getMessage());
		
		log.debug("Using view name: [{}]", modelView.getViewName());
		log.debug(
				"ModelAndView objects mapped: title[{}], message[{}]", 
				modelView.getModelMap().get("title"),
				modelView.getModelMap().get("message")
				);
		
		return modelView;
	}
	
	@PostConstruct
	private void logViews() {
		log.info("Assigned GENERIC_EXCEPTION_VIEW: [{}]", GENERIC_EXCEPTION_VIEW);
		log.info("Assigned SERVICE_EXCEPTION_VIEW: [{}]", SERVICE_EXCEPTION_VIEW);
	}
}
