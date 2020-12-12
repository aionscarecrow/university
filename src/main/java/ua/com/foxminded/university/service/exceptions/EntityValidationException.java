package ua.com.foxminded.university.service.exceptions;

public class EntityValidationException extends ServiceException {

	private static final long serialVersionUID = -686156799495466462L;

	public EntityValidationException(String msg) {
		super(msg);
	}

	public EntityValidationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
