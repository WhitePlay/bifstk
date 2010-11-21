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

	private Config() {
	}

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
	public String getCursorsPath() {
		return this.cursorsPath;
	}

	/**
	 * @param path the path to the directory containing cursor files
	 */
	public void setCursorsPath(String path) {
		this.cursorsPath = path;
	}

	private int displayFps;
	private static final int displayFpsMin = 10, displayFpsMax = 1000;

	/**
	 * @return Frames Per Second to display
	 */
	public int getDisplayFps() {
		return this.displayFps;
	}

	/**
	 * @param fps FPS to display
	 */
	public void setDisplayFps(int fps) {
		this.displayFps = Util.clampi(fps, displayFpsMin, displayFpsMax);
	}

	private boolean displayFpsCap;

	/**
	 * @return cap the FPS to {@link #getDisplayFps()} if true, unlimited if
	 *         false
	 */
	public boolean isDisplayFpsCap() {
		return this.displayFpsCap;
	}

	/**
	 * @param b cap the FPS to {@link #getDisplayFps()} or let it be unlimited
	 */
	public void setDisplayFpsCap(boolean b) {
		this.displayFpsCap = b;
	}

	private boolean displayFullScreen;

	/**
	 * @return ignore specified resolution and use native resolution fullscreen
	 */
	public boolean isDisplayFullscreen() {
		return this.displayFullScreen;
	}

	/**
	 * @param b use native resolution and fullscreen mode if true
	 */
	public void setDisplayFullScreen(boolean b) {
		this.displayFullScreen = b;
	}

	private int displayWidth, displayHeight;

	/**
	 * @return horizontal display resolution when not fullscreen
	 */
	public int getDisplayWidth() {
		return this.displayWidth;
	}

	/**
	 * @param h horizontal display resolution when not fullscreen
	 */
	public void setDisplayWidth(int w) {
		this.displayWidth = w;
	}

	/**
	 * @return vertical display resolution when not fullscreen
	 */
	public int getDisplayHeight() {
		return this.displayHeight;
	}

	/**
	 * @param h vertical display resolution when not fullscreen
	 */
	public void setDisplayHeight(int h) {
		this.displayHeight = h;
	}

	private String displayTitle;

	/**
	 * @return title of the display window in the OS running Bifstk
	 */
	public String getDisplayTitle() {
		return this.displayTitle;
	}

	/**
	 * @param title of the display window in the OS running Bifstk
	 */
	public void setDisplayTitle(String title) {
		this.displayTitle = title;
	}

	private boolean displayVsync;

	/**
	 * @return sync display refresh rate to the monitor's refresh rate if true
	 */
	public boolean isDisplayVsync() {
		return this.displayVsync;
	}

	/**
	 * @param vs sync display refresh rate to the monitor's refresh rate if true
	 */
	public void setDisplayVsync(boolean vs) {
		this.displayVsync = vs;
	}

	private int displayAntialiasSamples = 0;
	private int displayAntialiasSamplesMin = 0, displayAntialiasSamplesMax = 8;

	/**
	 * @return the number of antialias samples when Multisampling is enabled, or
	 *         0
	 */
	public int getDisplayAntialiasSamples() {
		return this.displayAntialiasSamples;
	}

	/**
	 * @param samples the number of antialias samples when Multisampling is
	 *            enabled, or 0
	 */
	public void setDisplayAntialiasSamples(int samples) {
		this.displayAntialiasSamples = Util.clampi(samples,
				displayAntialiasSamplesMin, displayAntialiasSamplesMax);
	}

	private String fontPath;

	/**
	 * @return path to the TTF file used for font rendering
	 */
	public String getFontPath() {
		return this.fontPath;
	}

	/**
	 * @param fp path to the TTF file used for font rendering
	 */
	public void setFontPath(String fp) {
		this.fontPath = fp;
	}

	private int fontSizeNormal;
	private static final int fontSizeMin = 4, fontSizeMax = 20;

	/**
	 * @return point size of the font in 'normal' size
	 */
	public int getFontSizeNormal() {
		return this.fontSizeNormal;
	}

	/**
	 * @param size point size of the font in 'normal' size
	 */
	public void setFontSizeNormal(int size) {
		this.fontSizeNormal = Util.clampi(size, fontSizeMin, fontSizeMax);
	}

	private int fontSizeSmall;

	/**
	 * @return point size of the font in 'small' size
	 */
	public int getFontSizeSmall() {
		return this.fontSizeSmall;
	}

	/**
	 * @param size point size of the font in 'small' size
	 */
	public void setFontSizeSmall(int size) {
		this.fontSizeSmall = Util.clampi(size, fontSizeMin, fontSizeMax);
	}

	private String loggerDateFormat;

	/**
	 * @return format of the logger date, see {@link DateFormat}
	 */
	public String getLoggerDateFormat() {
		return this.loggerDateFormat;
	}

	/**
	 * @param format format of the logger date, see {@link DateFormat}
	 */
	public void setLoggerDateFormat(String format) {
		this.loggerDateFormat = format;
	}

	private boolean loggerFileEnabled;

	/**
	 * @return true if logging to a file is enabled
	 */
	public boolean isLoggerFileEnabled() {
		return this.loggerFileEnabled;
	}

	/**
	 * @param e true if logging to a file is enabled
	 */
	public void setLoggerFileEnabled(boolean e) {
		this.loggerFileEnabled = e;
	}

	private boolean loggerFileOverwrite;

	/**
	 * @return true if the previous log files should be overwritten
	 */
	public boolean isLoggerFileOverwrite() {
		return this.loggerFileOverwrite;
	}

	/**
	 * @param b true if the previous log files should be overwritten
	 */
	public void setLoggerFileOverwrite(boolean b) {
		this.loggerFileOverwrite = b;
	}

	private String loggerFilePath;

	/**
	 * @return path to the log file
	 */
	public String getLoggerFilePath() {
		return this.loggerFilePath;
	}

	/**
	 * @param path path to the log file
	 */
	public void setLoggerFilePath(String path) {
		this.loggerFilePath = path;
	}

	private boolean loggerStdoutDebug;

	/**
	 * @return true if debug statements should be printed to stdout
	 */
	public boolean isLoggerStdoutDebug() {
		return this.loggerStdoutDebug;
	}

	/**
	 * @param d true if debug statements should be printed to stdout
	 */
	public void setLoggerStdoutDebug(boolean d) {
		this.loggerStdoutDebug = d;
	}

	private boolean loggerStdoutEnabled;

	/**
	 * @return true to if the log should be printed to stdout
	 */
	public boolean isLoggerStdoutEnabled() {
		return this.loggerStdoutEnabled;
	}

	/**
	 * @param b true to if the log should be printed to stdout
	 */
	public void setLoggerStdoutEnabled(boolean b) {
		this.loggerStdoutEnabled = b;
	}

	private boolean loggerStdoutTrace;

	/**
	 * @return true if a context trace should be printed to stdout
	 */
	public boolean isLoggerStdoutTrace() {
		return this.loggerStdoutTrace;
	}

	/**
	 * @param b true if a context trace should be printed to stdout
	 */
	public void setLoggerStdoutTrace(boolean b) {
		this.loggerStdoutTrace = b;
	}

	private int loggerTraceLength;
	private static final int loggerTraceLenMin = 15, loggerTraceLenMax = 60;

	/**
	 * @return length of the log context trace
	 */
	public int getLoggerTraceLength() {
		return this.loggerTraceLength;
	}

	/**
	 * @param len length of the log context trace
	 */
	public void setLoggerTraceLength(int len) {
		this.loggerTraceLength = Util.clampi(len, loggerTraceLenMin,
				loggerTraceLenMax);
	}

	private String themePath;

	/**
	 * @return path to the theme file
	 */
	public String getThemePath() {
		return this.themePath;
	}

	/**
	 * @param path path to the theme file
	 */
	public void setThemePath(String path) {
		this.themePath = path;
	}

	private boolean wmDebugLayout;

	/**
	 * @return true if debug information about the WM layout should be displayed
	 */
	public boolean isWmDebugLayout() {
		return this.wmDebugLayout;
	}

	/**
	 * @param d true if debug information about the WM layout should be
	 *            displayed
	 */
	public void setWmDebugLayout(boolean d) {
		this.wmDebugLayout = d;
	}

	private boolean wmFocusFollowmouse;

	/**
	 * @return true if the focus should follow the mouse cursor
	 */
	public boolean isWmFocusFollowmouse() {
		return this.wmFocusFollowmouse;
	}

	/**
	 * @param b true if the focus should follow the mouse cursor
	 */
	public void setWmFocusFollowmouse(boolean b) {
		this.wmFocusFollowmouse = b;
	}

	private boolean wmWindowSnapTop;

	/**
	 * @return true if top window drag-snap is enabled
	 */
	public boolean isWmWindowSnapTop() {
		return this.wmWindowSnapTop;
	}

	/**
	 * @param w true if top window drag-snap is enabled
	 */
	public void setWmWindowSnapTop(boolean w) {
		this.wmWindowSnapTop = w;
	}

	private boolean wmWindowDockLeft;

	/**
	 * @return true if the left dock is enabled
	 */
	public boolean isWmWindowDockLeft() {
		return this.wmWindowDockLeft;
	}

	/**
	 * @param b true if the left dock is enabled
	 */
	public void setWmWindowDockLeft(boolean b) {
		this.wmWindowDockLeft = b;
	}

	private boolean wmWindowDockRight;

	/**
	 * @return true if the right dock is enabled
	 */
	public boolean isWmWindowDockRight() {
		return this.wmWindowDockRight;
	}

	/**
	 * @param b true if the right dock is enabled
	 */
	public void setWmWindowDockRight(boolean b) {
		this.wmWindowDockRight = b;
	}

	private int wmFrameSizeMin;

	/**
	 * @return the minimum horizontal and vertical size of a Frame
	 */
	public int getWmFrameSizeMin() {
		return this.wmFrameSizeMin;
	}

	/**
	 * @param m the minimum horizontal and vertical size of a Frame
	 */
	public void setWmFrameSizeMin(int m) {
		this.wmFrameSizeMin = m;
	}

	private boolean wmFrameSnap;

	/**
	 * @return true if Frame snapping is activated (not edge resistance)
	 */
	public boolean isWmFrameSnap() {
		return this.wmFrameSnap;
	}

	/**
	 * @param b true if Frame snapping is activated (not edge resistance)
	 */
	public void setWmFrameSnap(boolean b) {
		this.wmFrameSnap = b;
	}

	private int wmFrameSnapRadius;

	/**
	 * @return the effect radius of Frame snapping
	 */
	public int getWmFrameSnapRadius() {
		return this.wmFrameSnapRadius;
	}

	/**
	 * @param r the effect radius of Frame snapping
	 */
	public void setWmFrameSnapRadius(int r) {
		this.wmFrameSnapRadius = r;
	}

	/**
	 * @return the radius of effect of frame snapping
	 */
	public int getFrameSnapRadius() {
		return this.wmFrameSnapRadius;
	}

	private boolean wmAnimations;

	/**
	 * @return true if WM animated transitions are enabled
	 */
	public boolean isWmAnimations() {
		return this.wmAnimations;
	}

	/**
	 * @param b true if WM animated transitions are enabled
	 */
	public void setWmAnimations(boolean b) {
		this.wmAnimations = b;
	}

	private int wmAnimationsLength;

	/**
	 * @return the duration of WM animated transitions when enabled in ms
	 */
	public int getWmAnimationsLength() {
		return this.wmAnimationsLength;
	}

	/**
	 * @param ms the duration of WM animated transitions when enabled in ms
	 */
	public void setWmAnimationsLength(int ms) {
		this.wmAnimationsLength = ms;
	}

	private void readProps(Map<ConfigProperty, String> props)
			throws BifstkException {
		for (Entry<ConfigProperty, String> prop : props.entrySet()) {
			String sval = prop.getValue();

			try {
				switch (prop.getKey()) {
				case cursorsPath:
					setCursorsPath(sval);
					break;
				case displayFps:
					setDisplayFps(Integer.parseInt(sval));
					break;
				case displayFpsCap:
					setDisplayFpsCap(Boolean.parseBoolean(sval));
					break;
				case displayFullscreen:
					setDisplayFullScreen(Boolean.parseBoolean(sval));
					break;
				case displayWidth:
					setDisplayWidth(Integer.parseInt(sval));
					break;
				case displayHeight:
					setDisplayHeight(Integer.parseInt(sval));
					break;
				case displayTitle:
					setDisplayTitle(sval.trim());
					break;
				case displayVsync:
					setDisplayVsync(Boolean.parseBoolean(sval));
					break;
				case displayAntialiasSamples:
					setDisplayAntialiasSamples(Integer.parseInt(sval));
					break;
				case fontPath:
					setFontPath(sval);
					break;
				case fontSizeNormal:
					setFontSizeNormal(Integer.parseInt(sval));
					break;
				case fontSizeSmall:
					setFontSizeSmall(Integer.parseInt(sval));
					break;
				case loggerDateFormat:
					setLoggerDateFormat(sval);
					break;
				case loggerFileEnabled:
					setLoggerFileEnabled(Boolean.parseBoolean(sval));
					break;
				case loggerFileOverwrite:
					setLoggerFileOverwrite(Boolean.parseBoolean(sval));
					break;
				case loggerFilePath:
					setLoggerFilePath(sval);
					break;
				case loggerStdoutDebug:
					setLoggerStdoutDebug(Boolean.parseBoolean(sval));
					break;
				case loggerStdoutEnabled:
					setLoggerStdoutEnabled(Boolean.parseBoolean(sval));
					break;
				case loggerStdoutTrace:
					setLoggerStdoutTrace(Boolean.parseBoolean(sval));
					break;
				case loggerTraceLength:
					setLoggerTraceLength(Integer.parseInt(sval));
					break;
				case themePath:
					setThemePath(sval);
					break;
				case wmDebugLayout:
					setWmDebugLayout(Boolean.parseBoolean(sval));
					break;
				case wmFocusFollowmouse:
					setWmFocusFollowmouse(Boolean.parseBoolean(sval));
					break;
				case wmWindowSnapTop:
					setWmWindowSnapTop(Boolean.parseBoolean(sval));
					break;
				case wmWindowDockLeft:
					setWmWindowDockLeft(Boolean.parseBoolean(sval));
					break;
				case wmWindowDockRight:
					setWmWindowDockRight(Boolean.parseBoolean(sval));
					break;
				case wmFrameSizeMin:
					setWmFrameSizeMin(Integer.parseInt(sval));
					break;
				case wmFrameSnap:
					setWmFrameSnap(Boolean.parseBoolean(sval));
					break;
				case wmFrameSnapRadius:
					setWmFrameSnapRadius(Integer.parseInt(sval));
					break;
				case wmAnimations:
					setWmAnimations(Boolean.parseBoolean(sval));
					break;
				case wmAnimationsLength:
					setWmAnimationsLength(Integer.parseInt(sval));
					break;
				}
			} catch (Throwable t) {
				throw new BifstkException("Could not read value for "
						+ prop.getKey().getProperty(), t);
			}

		}
	}

	/**
	 * @return the current global Config instance
	 */
	public static Config get() {
		return instance;
	}

	/**
	 * Loads an existing Config instance
	 * 
	 * @param conf the new current global Config instance
	 */
	public static void set(Config conf) {
		Config.instance = conf;
	}

	/**
	 * Creates a Config instance from a file descriptor
	 * 
	 * @param path path to a local file containing definitions for all
	 *            {@link bifstk.config.ConfigProperty}
	 * @return a Bifstk Config instance that can be loaded through
	 *         {@link #setInstance(Config)} or stored
	 * @throws BifstkException
	 */
	public static Config load(String path) throws BifstkException {
		return new Config(new File(path));
	}

}
