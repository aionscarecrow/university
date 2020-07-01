package ua.com.foxminded.university.domain.exceptions;

public class DomainException extends Exception {

	private static final long serialVersionUID = 131637876982956135L;

	public DomainException(String message) {
		super(message);
	}

	public DomainException(String message, Throwable cause) {
		super(message, cause);
	}

}
