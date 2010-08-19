package bifstk.config;

/**
 * Defines a set of properties used to configure Bifstk
 * 
 * 
 */
public enum Property {

	// ///////////////////
	// /// Logging
	// ///////////////////
	/** Enable or disable printing logs to a file */
	loggerFileEnabled("logger.file.enabled"),
	/** Path of the log file on the local filesystem */
	loggerFilePath("logger.file.path"),
	/** Overwrite log file */
	loggerFileOverwrite("logger.file.overwrite"),
	/** Enable or disable printing logs to stdout */
	loggerStdoutEnabled("logger.stdout.enabled"),
	/** Print debug statements to stdout */
	loggerStdoutDebug("logger.stdout.debug"),
	/** Print caller method context to stdout */
	loggerStdoutTrace("logger.stdout.trace"),
	/** length of the contextual trace for each line */
	loggerTraceLength("logger.trace.length"),
	/** format of the date of each log element */
	loggerDateFormat("logger.date.format"),

	// ///////////////////
	// /// Display
	// ///////////////////
	/** Horizontal pixel width of the display */
	displayWidth("display.width"),
	/** Horizontal pixel height of the display */
	displayHeight("display.height"),
	/** True to enable fullscreen display */
	displayFullscreen("display.fullscreen"),
	/** Title of the display used by the window manager */
	displayTitle("display.title"),
	/** refresh rate of the display in frames per second */
	displayFps("display.fps"),
	/** do not cap the framerate if false */
	displayFpsCap("display.fps.cap"),
	/** enable VSync */
	displayVsync("display.vsync"),

	// /////////////////////////
	// /// Fonts
	// /////////////////////////
	/** Path to the ttf font file */
	fontPath("font.path"),
	/** Point size of the normal font size */
	fontSizeSmall("font.size.small"),
	/** point size of the small font size */
	fontSizeNormal("font.size.normal"),

	// //////////////////////////
	// /// Graphical resources
	// //////////////////////////
	/** Path to the directory containing the mouse cursor bitmaps */
	cursorsPath("gfx.cursors.path"),
	/** Path to the theme file */
	themePath("gfx.theme.path"),

	// ///////////////////////////
	// //// Window manager
	// ///////////////////////////
	/** true if frame focus follows the mouse or if click is required */
	wmFocuseFollowmouse("wm.focus.followmouse"),
	/** displays some visual debug info in the UI layout when true */
	wmDebugLayout("wm.debug.layout");

	private String property = null;

	private Property(String property) {
		this.property = property;
	}

	/**
	 * @return the name of the property as a formatted String
	 */
	public String getProperty() {
		return this.property;
	}
}
