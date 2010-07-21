package bifstk.util;

import org.newdawn.slick.util.LogSystem;

/**
 * Wraps Slick's {@link LogSystem} in Bifstk's {@link Logger}
 * 
 */
public class BifstkLogSystem implements LogSystem {

	private static final String prefix = "[Slick] ";

	@Override
	public void error(String message, Throwable e) {
		Logger.error(prefix + message + " " + e.getMessage());
	}

	@Override
	public void error(Throwable e) {
		Logger.error(prefix + e.getMessage());
	}

	@Override
	public void error(String message) {
		Logger.error(prefix + message);
	}

	@Override
	public void warn(String message) {
		Logger.warn(prefix + message);
	}

	@Override
	public void warn(String message, Throwable e) {
		Logger.warn(prefix + message + " " + e.getMessage());
	}

	@Override
	public void info(String message) {
		Logger.info(prefix + message);
	}

	@Override
	public void debug(String message) {
		Logger.debug(prefix + message);
	}

}
