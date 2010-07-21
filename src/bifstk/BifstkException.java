package bifstk;

public class BifstkException extends Exception {

	private static final long serialVersionUID = 1L;

	public BifstkException() {
		super();
	}

	public BifstkException(String message) {
		super(message);
	}

	public BifstkException(Throwable cause) {
		super(cause);
	}

	public BifstkException(String message, Throwable cause) {
		super(message, cause);
	}

}
