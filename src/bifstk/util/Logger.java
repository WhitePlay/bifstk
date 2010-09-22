package bifstk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import bifstk.config.Config;

/**
 * Simple and straightforward logging facility
 * <p>
 * Allows logging to a file and/or stdout, accessing through a singleton
 * pattern.
 * 
 */
public class Logger {

	/** File to which the log is appended */
	private PrintStream out;
	/** Path to the file to which the log is appended */
	private String outPath;

	private final static String separator = System
			.getProperty("line.separator");

	/**
	 * Determines the category and importance of a log message
	 */
	public enum Level {
		/** a negative event that will most likely impact the execution */
		ERROR,
		/** a negative event that should not impact the execution */
		WARN,
		/** an event that should occur upon normal execution */
		INFO,
		/** additional information that should not be useful to end users */
		DEBUG;
	}

	/** print DEBUG level messages if true */
	private boolean debug = false;
	/** print trace in stdout messages */
	private boolean trace = false;
	/** trace max length */
	private int traceLen = 25;

	/**
	 * Visibility of a log event
	 */
	public enum Visibility {
		/** events should be appended to the log file */
		FILE,
		/** events should be printed on stdout */
		STDOUT,
		/** events should be printed in both file and stdout */
		BOTH,
		/** all events are dropped */
		NONE;

		/**
		 * @return true if this level of visibility outputs to a log file
		 */
		public boolean isFile() {
			return (this.equals(Visibility.FILE) || this
					.equals(Visibility.BOTH));
		}

		/**
		 * @return true if this level of visibility outputs to stdout
		 */
		public boolean isStdout() {
			return (this.equals(Visibility.STDOUT) || this
					.equals(Visibility.BOTH));
		}
	}

	private Visibility visibility;

	/** Singleton instance */
	private static Logger instance = null;

	/**
	 * Private constructor, access should be static only
	 * 
	 * @param output
	 * @param stdout
	 * @param debug
	 * @param trace
	 * @param stamps
	 * @param overwrite
	 * @param len
	 */
	private Logger(Visibility vis, String output, boolean debug, boolean trace,
			boolean overwrite, int tracelen) {
		this.outPath = output;
		this.visibility = vis;
		this.debug = debug;
		this.trace = trace;
		this.traceLen = tracelen;

		// log should append to file, trying to open a log file
		if (this.visibility.isFile()) {
			try {
				// open the specified file
				this.out = new PrintStream(new FileOutputStream(
						new File(output), overwrite));
			} catch (Exception e) {
				try {
					// could not open the specified file, trying a
					// temporary one instead
					this.outPath = System.getProperty("java.io.tmpdir")
							+ File.separator + "bifstk-"
							+ System.currentTimeMillis() + ".log";
					this.out = new PrintStream(new FileOutputStream(
							this.outPath));

					System.out.println("! Could not open log file '" + output
							+ "', appending log to " + this.outPath);
				} catch (Exception ex) {
					// log file could not be opened, disabling file output
					this.outPath = null;
					this.out = null;
					System.out.println("! Could not open log file '" + output
							+ "'");
					if (this.visibility.isStdout()) {
						System.out
								.println("Logs will be printed on STDOUT only");
						this.visibility = Visibility.STDOUT;
					} else {
						System.out.println("All logs will be dropped");
						this.visibility = Visibility.NONE;
					}
				}
			}
		}
	}

	/**
	 * Initializes the logging facility
	 */
	public static void init() {
		Visibility vis = Visibility.NONE;

		boolean confFile = Config.isLoggerFileEnabled();
		boolean confOut = Config.isLoggerStdoutEnabled();

		if (confFile && confOut) {
			vis = Visibility.BOTH;
		} else if (confFile) {
			vis = Visibility.FILE;
		} else if (confOut) {
			vis = Visibility.STDOUT;
		}
		String out = Config.getLoggerFilePath();
		boolean debug = Config.isLoggerStdoutDebug();
		boolean trace = Config.isLoggerStdoutTrace();
		boolean overwrite = Config.isLoggerFileOverwrite();
		int len = Config.getLoggerTraceLength();

		Logger.instance = new Logger(vis, out, debug, trace, overwrite, len);

		instance.message("--- Log begins --------", Level.INFO, Visibility.FILE);
	}

	/**
	 * Issue an error message
	 * 
	 * @param message the message to append to the log
	 */
	public static void error(String message) {
		check();
		instance.message("[E] " + message, Level.ERROR);
	}

	/**
	 * Issue an error message
	 * 
	 * @param message the message to append to the log
	 * @param t the Exception to append to the log
	 */
	public static void error(String message, Throwable t) {
		check();
		instance.message("[E] " + message, Level.ERROR, t);
	}

	/**
	 * Issue a warning message
	 * 
	 * @param message the message to append to the log
	 */
	public static void warn(String message) {
		check();
		instance.message("[W] " + message, Level.WARN);
	}

	/**
	 * Issue a warning message
	 * 
	 * @param t the Exception to append to the log
	 */
	public static void warn(String message, Throwable t) {
		check();
		instance.message("[W] " + message, Level.WARN, t);
	}

	/**
	 * Issue a debug message
	 * 
	 * @param message the message to append to the log
	 */
	public static void debug(String message) {
		check();
		instance.message("[D] " + message, Level.DEBUG);
	}

	/**
	 * Issue a debug message
	 * 
	 * @param message the message to append to the log
	 * @param t the Exception to append to the log
	 */
	public static void debug(String message, Throwable t) {
		check();
		instance.message("[D] " + message, Level.DEBUG, t);
	}

	/**
	 * Issue an information message
	 * 
	 * @param message the message to append to the log
	 */
	public static void info(String message) {
		check();
		instance.message("    " + message, Level.INFO);
	}

	/**
	 * Throws a RuntimeException if the logger is not ready to be used
	 */
	private static void check() {
		if (Logger.instance == null)
			throw new IllegalStateException("Logger has not be initialized.");
	}

	/**
	 * Append a message to the log
	 * 
	 * @param message the message to print
	 * @param l the level of the message
	 */
	private void message(String message, Level l) {
		message(message, l, this.visibility);
	}

	/**
	 * Append a message to the log
	 * 
	 * @param message the message to print
	 * @param l the level of the message
	 * @param t the Exception to append to the log
	 */
	private void message(String message, Level l, Throwable t) {
		message(message, l, this.visibility, t);
	}

	/**
	 * Append a message to the log
	 * 
	 * @param message the message to print
	 * @param l the level of the message
	 * @param vis visibility of the message
	 * @param t the Exception to append to the log
	 */
	private void message(String message, Level l, Visibility vis) {
		message(message, l, vis, null);
	}

	private void message(String message, Level l, Visibility vis, Throwable t) {
		switch (vis) {
		case FILE:
			if (!this.visibility.isFile())
				return;
			break;
		case STDOUT:
			if (!this.visibility.isStdout())
				return;
			break;
		case NONE:
			return;
		case BOTH:
			if (!this.visibility.equals(Visibility.BOTH))
				return;
			break;
		}
		if (l.equals(Level.DEBUG) && !this.debug) {
			// debug is disabled
			return;
		}
		if (vis.isStdout()) {
			System.out.println(getFormattedString(message, t, false,
					this.trace, this.traceLen));
		}
		if (vis.isFile()) {
			this.out.println(getFormattedString(message, t, true, true,
					this.traceLen));
		}
	}

	private static String getFormattedString(String message, Throwable t,
			boolean stamps, boolean trace, int traceLen) {
		String ret = "";
		String prefix = "";

		if (stamps) {
			prefix += DateHelper.getTimeStamps() + " ";
		}
		if (trace) {
			prefix += getContext(traceLen);
		}
		if (prefix.length() != 0) {
			prefix = "[" + prefix + "] ";
		}

		ret += prefix + message;

		while (t != null) {
			String filler = new String();
			for (int i = 0; i < prefix.length() + 4; i++) {
				filler += " ";
			}

			ret += separator + filler;
			ret += t.getClass().getName();
			if (t.getMessage() != null && t.getMessage().trim().length() > 0) {
				ret += ": " + t.getMessage();
			}

			for (StackTraceElement el : t.getStackTrace()) {
				ret += separator + filler + "` " + el.toString();
			}

			t = t.getCause();
		}
		return ret;
	}

	/**
	 * Provides a String representing the caller method context : class and
	 * method. String is fixed length to improve readability
	 * 
	 * @param length length of the returned String
	 * @return a String representation of the caller method context
	 */
	private static String getContext(int length) {
		String context = "";
		try {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			StackTraceElement e = null;
			int i = 4;
			while (true) {
				if (i >= stack.length) {
					break;
				}
				String sname = stack[i].getClassName();
				if (sname.matches(".*Log.*")) {
					i++;
					continue;
				} else {
					e = stack[i];
					break;
				}
			}
			if (e == null) {
				context = "...";
			} else {
				context = e.getClassName() + "#" + e.getMethodName();
			}
		} catch (Exception e) {
			context = "...";
		}

		if (length < 1) {
			throw new IllegalArgumentException("Less than " + length
					+ " characters would prove counter-productive,"
					+ "try a reasonnable value");
		}

		while (context.length() < length) {
			context += " ";
		}
		if (context.length() > length) {
			context = ".."
					+ context.substring(context.length() - length + 2,
							context.length());
		}

		return context;
	}
}
