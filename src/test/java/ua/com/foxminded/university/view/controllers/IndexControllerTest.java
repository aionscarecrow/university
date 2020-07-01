package ua.com.foxminded.university.view.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.service.exceptions.ServiceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Index Controller")
class IndexControllerTest {
	
	@Mock
	private HttpServletRequest request;
	
	private IndexController indexController = new IndexController();

	@Test
	@DisplayName("returns proper view")
	public void testReturnProperView() throws ServiceException {
		when(request.getRemoteAddr()).thenReturn("192.168.127.170");
		String expectedView = IndexController.INDEX_VIEW;
		String actualView = indexController.getIndexPage(request);
		
		assertEquals(expectedView, actualView);
	}

}
