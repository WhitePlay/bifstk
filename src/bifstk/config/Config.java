package bifstk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import bifstk.gl.Util;
import bifstk.util.BifstkException;

/**
 * Bifstk's Configuration facility
 * <p>
 * Access to configuration properties can be performed statically with
 * {@link #getValue(ConfigProperty)} after an initial call to
 * {@link #load(String)}
 * 
 * 
 */
public class Config {

	/** singleton instance */
	private static Config instance = null;

	/** configuration file */
	private File config = null;

	/** values for all {@link bifstk.config.ConfigProperty} */
	private Map<ConfigProperty, String> properties = null;

	/**
	 * Default constructor
	 * 
	 * @param conf path to the configuration file
	 * @throws BifstkException the configuration was not loaded
	 */
	private Config(File conf) throws BifstkException {
		this.config = conf;
		this.properties = new HashMap<ConfigProperty, String>();
		this.loadConfig();
	}

	/**
	 * Loads the configuration from the file
	 * 
	 * @throws BifstkException the file could not be read, or it is incomplete
	 */
	private void loadConfig() throws BifstkException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(this.config);
		} catch (FileNotFoundException e) {
			throw new BifstkException("Could not find configuration file", e);
		}

		Properties props = new Properties();
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			throw new BifstkException("Could not read configuration file", e);
		}

		try {
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				for (ConfigProperty p : ConfigProperty.values()) {
					if (p.getProperty().equals(key)) {
						properties.put(p, (String) entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			throw new BifstkException("Error parsing configuration file", e);
		}

		for (ConfigProperty p : ConfigProperty.values()) {
			if (!this.properties.containsKey(p)) {
				String message = "Property " + p + " (" + p.getProperty()
						+ ") is not defined in configuration";
				throw new BifstkException(message);
			}
		}

		readProps(properties);
	}

	private String cursorsPath;

	/**
	 * @return path to the directory containing cursor files
	 */
	public static String getCursorsPath() {
		check();
		return instance.cursorsPath;
	}

	private int displayFps;
	private static final int displayFpsMin = 10, displayFpsMax = 1000;

	/**
	 * @return Frames Per Second to display
	 */
	public static int getDisplayFps() {
		check();
		return instance.displayFps;
	}

	private boolean displayFpsCap;

	/**
	 * @return cap the FPS to {@link #getDisplayFps()} if true, unlimited if
	 *         false
	 */
	public static boolean isDisplayFpsCap() {
		check();
		return instance.displayFpsCap;
	}

	private boolean displayFullScreen;

	/**
	 * @return ignore specified resolution and use native resolution fullscreen
	 */
	public static boolean isDisplayFullscreen() {
		check();
		return instance.displayFullScreen;
	}

	private int displayWidth, displayHeight;

	/**
	 * @return horizontal display resolution when not fullscreen
	 */
	public static int getDisplayWidth() {
		check();
		return instance.displayWidth;
	}

	/**
	 * @return vertical display resolution when not fullscreen
	 */
	public static int getDisplayHeight() {
		check();
		return instance.displayHeight;
	}

	private String displayTitle;

	/**
	 * @return title of the display window in the OS running Bifstk
	 */
	public static String getDisplayTitle() {
		check();
		return instance.displayTitle;
	}

	private boolean displayVsync;

	/**
	 * @return sync display refresh rate to the monitor's refresh rate if true
	 */
	public static boolean isDisplayVsync() {
		check();
		return instance.displayVsync;
	}

	private String fontPath;

	/**
	 * @return path to the TTF file used for font rendering
	 */
	public static String getFontPath() {
		check();
		return instance.fontPath;
	}

	private int fontSizeNormal;
	private static final int fontSizeMin = 4, fontSizeMax = 20;

	/**
	 * @return point size of the font in 'normal' size
	 */
	public static int getFontSizeNormal() {
		check();
		return instance.fontSizeNormal;
	}

	private int fontSizeSmall;

	/**
	 * @return point size of the font in 'small' size
	 */
	public static int getFontSizeSmall() {
		check();
		return instance.fontSizeSmall;
	}

	private String loggerDateFormat;

	/**
	 * @return format of the logger date, see {@link DateFormat}
	 */
	public static String getLoggerDateFormat() {
		check();
		return instance.loggerDateFormat;
	}

	private boolean loggerFileEnabled;

	/**
	 * @return true if logging to a file is enabled
	 */
	public static boolean isLoggerFileEnabled() {
		check();
		return instance.loggerFileEnabled;
	}

	private boolean loggerFileOverwrite;

	/**
	 * @return true if the previous log files should be overwritten
	 */
	public static boolean isLoggerFileOverwrite() {
		check();
		return instance.loggerFileOverwrite;
	}

	private String loggerFilePath;

	/**
	 * @return path to the log file
	 */
	public static String getLoggerFilePath() {
		check();
		return instance.loggerFilePath;
	}

	private boolean loggerStdoutDebug;

	/**
	 * @return true if debug statements should be printed to stdout
	 */
	public static boolean isLoggerStdoutDebug() {
		check();
		return instance.loggerStdoutDebug;
	}

	private boolean loggerStdoutEnabled;

	/**
	 * @return true to if the log should be printed to stdout
	 */
	public static boolean isLoggerStdoutEnabled() {
		check();
		return instance.loggerStdoutEnabled;
	}

	private boolean loggerStdoutTrace;

	/**
	 * @return true if a context trace should be printed to stdout
	 */
	public static boolean isLoggerStdoutTrace() {
		check();
		return instance.loggerStdoutTrace;
	}

	private int loggerTraceLength;
	private static final int loggerTraceLenMin = 15, loggerTraceLenMax = 60;

	/**
	 * @return length of the log context trace
	 */
	public static int getLoggerTraceLength() {
		check();
		return instance.loggerTraceLength;
	}

	private String themePath;

	/**
	 * @return path to the theme file
	 */
	public static String getThemePath() {
		check();
		return instance.themePath;
	}

	private boolean wmDebugLayout;

	/**
	 * @return true if debug information about the WM layout should be displayed
	 */
	public static boolean isWmDebugLayout() {
		check();
		return instance.wmDebugLayout;
	}

	private boolean wmFocusFollowmouse;

	/**
	 * @return true if the focus should follow the mouse cursor
	 */
	public static boolean isWmFocusFollowmouse() {
		check();
		return instance.wmFocusFollowmouse;
	}

	private void readProps(Map<ConfigProperty, String> props)
			throws BifstkException {
		for (Entry<ConfigProperty, String> prop : props.entrySet()) {
			String sval = prop.getValue();

			try {
				switch (prop.getKey()) {
				case cursorsPath:
					this.cursorsPath = sval;
					break;
				case displayFps:
					this.displayFps = Util.clampi(Integer.parseInt(sval),
							displayFpsMin, displayFpsMax);
					break;
				case displayFpsCap:
					this.displayFpsCap = Boolean.parseBoolean(sval);
					break;
				case displayFullscreen:
					this.displayFullScreen = Boolean.parseBoolean(sval);
					break;
				case displayHeight:
					this.displayHeight = Integer.parseInt(sval);
					break;
				case displayTitle:
					this.displayTitle = sval.trim();
					break;
				case displayVsync:
					this.displayVsync = Boolean.parseBoolean(sval);
					break;
				case displayWidth:
					this.displayWidth = Integer.parseInt(sval);
					break;
				case fontPath:
					this.fontPath = sval;
					break;
				case fontSizeNormal:
					this.fontSizeNormal = Util.clampi(Integer.parseInt(sval),
							fontSizeMin, fontSizeMax);
					break;
				case fontSizeSmall:
					this.fontSizeSmall = Util.clampi(Integer.parseInt(sval),
							fontSizeMin, fontSizeMax);
					break;
				case loggerDateFormat:
					this.loggerDateFormat = sval;
					break;
				case loggerFileEnabled:
					this.loggerFileEnabled = Boolean.parseBoolean(sval);
					break;
				case loggerFileOverwrite:
					this.loggerFileOverwrite = Boolean.parseBoolean(sval);
					break;
				case loggerFilePath:
					this.loggerFilePath = sval;
					break;
				case loggerStdoutDebug:
					this.loggerStdoutDebug = Boolean.parseBoolean(sval);
					break;
				case loggerStdoutEnabled:
					this.loggerStdoutEnabled = Boolean.parseBoolean(sval);
					break;
				case loggerStdoutTrace:
					this.loggerStdoutTrace = Boolean.parseBoolean(sval);
					break;
				case loggerTraceLength:
					this.loggerTraceLength = Util.clampi(
							Integer.parseInt(sval), loggerTraceLenMin,
							loggerTraceLenMax);
					break;
				case themePath:
					this.themePath = sval;
					break;
				case wmDebugLayout:
					this.wmDebugLayout = Boolean.parseBoolean(sval);
					break;
				case wmFocusFollowmouse:
					this.wmFocusFollowmouse = Boolean.parseBoolean(sval);
					break;

				}
			} catch (Throwable t) {
				throw new BifstkException("Could not read value for "
						+ prop.getKey().getProperty(), t);
			}

		}
	}

	private static void check() throws IllegalStateException {
		if (instance == null) {
			throw new IllegalStateException("Config has not been loaded");
		}
	}

	/**
	 * Statically loads the configuration from a file
	 * <p>
	 * Will create the singleton instance and enable access to property values
	 * through the appropriate public accessor
	 * 
	 * @param path path to a local file containing definitions for all
	 *            {@link bifstk.config.ConfigProperty}
	 * @throws BifstkException the configuration was not loaded
	 */
	public static void load(String path) throws BifstkException {
		Config.instance = new Config(new File(path));
	}

}
