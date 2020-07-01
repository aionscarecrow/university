package ua.com.foxminded.university.service.exceptions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 5098331977245309582L;

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
