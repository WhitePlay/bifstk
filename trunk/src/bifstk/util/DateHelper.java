package bifstk.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bifstk.config.Config;
import bifstk.config.Property;

/**
 * Utility to help manipulate dates
 * <p>
 * 
 */
public class DateHelper {

	private final static String defaultFormat = "yy-MM-dd hh:mm:ss.SSS";

	/**
	 * Timestamps formatter
	 */
	private static DateFormat timeStamps = null;
	/**
	 * Singleton instance
	 */
	private static DateHelper instance = null;

	/**
	 * Constructor
	 * <p>
	 * Access to non-static class members is restricted
	 */
	private DateHelper() {
		String format = Config.getValue(Property.loggerDateFormat);
		try {
			timeStamps = new SimpleDateFormat(format);
		} catch (Exception e) {
			timeStamps = new SimpleDateFormat(defaultFormat);
		}
	}

	/**
	 * Internal init
	 */
	private static void init() {
		if (instance == null) {
			instance = new DateHelper();
		}
	}

	/**
	 * @return a String representation of the current date, suited for log
	 *         timestamps
	 */
	public static String getTimeStamps() {
		init();

		Date now = new Date();
		return timeStamps.format(now);
	}

	/**
	 * @return the number of minutes since epoch
	 */
	public static long getMinutesSinceEpoch() {
		return (System.currentTimeMillis() / 1000);
	}
}
