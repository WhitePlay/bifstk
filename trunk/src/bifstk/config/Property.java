package bifstk.config;

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

	// //////////////////////////
	// /// Graphical resources
	// //////////////////////////
	/** Path to the directory containing the mouse cursor bitmaps */
	cursorsPath("gfx.cursors.path");

	private String property = null;

	private Property(String property) {
		this.property = property;
	}

	public String getProperty() {
		return this.property;
	}
}
