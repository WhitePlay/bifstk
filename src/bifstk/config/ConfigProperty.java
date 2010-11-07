package bifstk.config;

/**
 * Defines a set of properties used to configure the Bifstk runtime
 * 
 * 
 */
public enum ConfigProperty {

	// ///////////////////
	// /// Logging
	// ///////////////////
	/** BOOL Enable or disable printing logs to a file */
	loggerFileEnabled("logger.file.enabled"),
	/** STRING Path of the log file on the local filesystem */
	loggerFilePath("logger.file.path"),
	/** BOOL Overwrite log file */
	loggerFileOverwrite("logger.file.overwrite"),
	/** BOOL Enable or disable printing logs to stdout */
	loggerStdoutEnabled("logger.stdout.enabled"),
	/** BOOL Print debug statements to stdout */
	loggerStdoutDebug("logger.stdout.debug"),
	/** BOOL Print caller method context to stdout */
	loggerStdoutTrace("logger.stdout.trace"),
	/** INT length of the contextual trace for each line */
	loggerTraceLength("logger.trace.length"),
	/** STRING format of the date of each log element */
	loggerDateFormat("logger.date.format"),

	// ///////////////////
	// /// Display
	// ///////////////////
	/** INT Horizontal pixel width of the display */
	displayWidth("display.width"),
	/** INT Horizontal pixel height of the display */
	displayHeight("display.height"),
	/** BOOL True to enable fullscreen display */
	displayFullscreen("display.fullscreen"),
	/** STRING Title of the display used by the window manager */
	displayTitle("display.title"),
	/** INT refresh rate of the display in frames per second */
	displayFps("display.fps"),
	/** BOOL do not cap the framerate if false */
	displayFpsCap("display.fps.cap"),
	/** BOOL enable VSync */
	displayVsync("display.vsync"),
	/** INT number of antialias samples for Multisampling */
	displayAntialiasSamples("display.antialias.samples"),

	// /////////////////////////
	// /// Fonts
	// /////////////////////////
	/** STRING Path to the ttf font file */
	fontPath("font.path"),
	/** INT Point size of the normal font size */
	fontSizeSmall("font.size.small"),
	/** INT point size of the small font size */
	fontSizeNormal("font.size.normal"),

	// //////////////////////////
	// /// Graphical resources
	// //////////////////////////
	/** STRING Path to the directory containing the mouse cursor bitmaps */
	cursorsPath("gfx.cursors.path"),
	/** STRING Path to the theme file */
	themePath("gfx.theme.path"),

	// ///////////////////////////
	// //// Window manager
	// ///////////////////////////
	/** BOOL true if frame focus follows the mouse or if click is required */
	wmFocusFollowmouse("wm.focus.followmouse"),
	/** BOOL displays some visual debug info in the UI layout when true */
	wmDebugLayout("wm.debug.layout"),
	/** BOOL true to enable maximization on top window drag */
	wmWindowSnapTop("wm.window.snap.top"),
	/** BOOL enable the left dock */
	wmWindowDockLeft("wm.window.dock.left"),
	/** BOOL enable the right dock */
	wmWindowDockRight("wm.window.dock.right"),
	/** INT minimum width/height of a frame, borders included */
	wmFrameSizeMin("wm.frame.size.min"),
	/** BOOL enable edge snapping (not edge resistance) */
	wmFrameSnap("wm.frame.snap"),
	/** INT radius of the edge snapping */
	wmFrameSnapRadius("wm.frame.snap.radius"),
	/** BOOL enable animations */
	wmAnimations("wm.animations"),
	/** INT length in milliseconds for animations */
	wmAnimationsLength("wm.animations.length");

	private String property = null;

	private ConfigProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the name of the property as a formatted String
	 */
	public String getProperty() {
		return this.property;
	}
}
