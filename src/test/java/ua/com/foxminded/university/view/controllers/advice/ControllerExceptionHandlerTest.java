package ua.com.foxminded.university.view.controllers.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.university.service.exceptions.ServiceException;

@DisplayName("Controller Exception Handler")
class ControllerExceptionHandlerTest {
	
	private ControllerExceptionHandler handler = new ControllerExceptionHandler();
	
	@Test
	@DisplayName("returns proper view for ServiceException")
	void testServiceExceptionView() {
		ServiceException e = new ServiceException("Test exception");
		String expectedView = ControllerExceptionHandler.SERVICE_EXCEPTION_VIEW;
		String actualView = handler.handleServiceException(e).getViewName();
		
		assertEquals(expectedView, actualView);
	}
	
	@Test
	@DisplayName("returns proper view for generic Exception")
	void testGenricExceptionView() {
		Exception e = new Exception("Test exception");
		String expectedView = ControllerExceptionHandler.GENERIC_EXCEPTION_VIEW;
		String actualView = handler.handleGenericException(e).getViewName();
		
		assertEquals(expectedView, actualView);
	}
	
	@Test
	@DisplayName("returns proper message for ServiceException")
	void testServiceExceptionMessage() {
		ServiceException e = new ServiceException("Test exception");
		String expectedMessage = e.getMessage();
		String actualMessage = 
				(String)handler.handleServiceException(e).getModelMap().get("message");
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	@DisplayName("returns proper message for generic Exception")
	void testGenericExceptionMessage() {
		Exception e = new Exception("Test exception");
		String expectedMessage = e.getMessage();
		String actualMessage = 
				(String)handler.handleGenericException(e).getModelMap().get("message");
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	@DisplayName("returns proper title for ServiceException")
	void testServiceExceptionTitle() {
		ServiceException e = new ServiceException("Test exception");
		String expectedTitle = e.getClass().getSimpleName();
		String actualTitle = 
				(String)handler.handleServiceException(e).getModelMap().get("title");
		
		assertEquals(expectedTitle, actualTitle);
	}
	
	@Test
	@DisplayName("returns proper title for generic Exception")
	void testGenericExceptionTitle() {
		Exception e = new Exception("Test exception");
		String expectedTitle = e.getClass().getSimpleName();
		String actualTitle = 
				(String)handler.handleGenericException(e).getModelMap().get("title");
		
		assertEquals(expectedTitle, actualTitle);
	}
}
