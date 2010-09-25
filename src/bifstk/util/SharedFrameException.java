package bifstk.util;

/**
 * A Frame is used in concurrent contexts in the Window Manager
 * <p>
 * A Frame should always be inserted and displayed at one single point in the
 * system
 * 
 */
public class SharedFrameException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param message exception message
	 */
	public SharedFrameException(String message) {
		super(message);
	}

	/**
	 * Default constructor
	 * 
	 * @param message exception message
	 * @param e exception cause
	 */
	public SharedFrameException(String message, Exception e) {
		super(message, e);
	}
}
