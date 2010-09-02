package bifstk.util;

/**
 * Thrown when accessing a method from the wrong execution context
 */
public class ThreadAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param message informative message
	 * @param cause chained exception
	 */
	public ThreadAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Default constructor
	 * 
	 * @param message informative message
	 */
	public ThreadAccessException(String message) {
		super(message);
	}

}
