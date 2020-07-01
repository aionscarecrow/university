package ua.com.foxminded.university.dao.exceptions;

public class DaoException extends Exception {

	private static final long serialVersionUID = 8509589211806565132L;

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
