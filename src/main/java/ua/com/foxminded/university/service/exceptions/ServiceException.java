package ua.com.foxminded.university.service.exceptions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 5098331977245309582L;

	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
