package bifstk.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import bifstk.BifstkException;
import bifstk.gl.Color;
import bifstk.util.Logger;

/**
 * Configurable UI properties
 * <p>
 * Properties are held statically after being loaded
 * 
 */
public class Theme {

	private Color rootBackgroundColor = null;

	/**
	 * @return root background color
	 */
	public static Color getRootBackgroundColor() {
		return instance.rootBackgroundColor;
	}

	private int frameBorderWidth;
	private int frameBorderWidthMin = 1, frameBorderWidthMax = 10;

	/**
	 * @return pixel width of the border around the frame
	 */
	public static int getFrameBorderWidth() {
		return instance.frameBorderWidth;
	}

	private Color frameBorderFocusedColor = null;

	public static Color getFrameBorderFocusedColor() {
		return instance.frameBorderFocusedColor;
	}

	private Color frameBorderUnfocusedColor = null;

	public static Color getFrameBorderUnfocusedColor() {
		return instance.frameBorderUnfocusedColor;
	}

	private Color frameTitlebarFocusedColor = null;

	public static Color getFrameTitlebarFocusedColor() {
		return instance.frameTitlebarFocusedColor;
	}

	private Color frameTitlebarUnfocusedColor = null;

	public static Color getFrameTitlebarUnfocusedColor() {
		return instance.frameTitlebarUnfocusedColor;
	}

	private float frameMovedAlpha;

	/**
	 * @return frame opacity when moved
	 */
	public static float getFrameMovedAlpha() {
		return instance.frameMovedAlpha;
	}

	private float frameResizedAlpha;

	/**
	 * @return frame opacity when resized
	 */
	public static float getFrameResizedAlpha() {
		return instance.frameResizedAlpha;
	}

	private float frameUnfocusedAlpha;

	/**
	 * @return frame opacity when not focused
	 */
	public static float getFrameUnfocusedAlpha() {
		return instance.frameUnfocusedAlpha;
	}

	private boolean frameShadowEnabled;

	/**
	 * @return true to enable dropped shadows around frames
	 */
	public static boolean isFrameShadowEnabled() {
		return instance.frameShadowEnabled;
	}

	private float frameShadowAlpha;

	/**
	 * @return opacity of the frame shadow
	 */
	public static float getFrameShadowAlpha() {
		return instance.frameShadowAlpha;
	}

	private int frameShadowRadius;
	private int frameShadowRadiusMin = 1, frameShadowRadiusMax = 20;

	/**
	 * @return pixel radius of the frame shadow
	 */
	public static int getFrameShadowRadius() {
		return instance.frameShadowRadius;
	}

	private Color uiBgColor = null;

	/**
	 * @return the background color of the ui
	 */
	public static Color getUiBgColor() {
		return instance.uiBgColor;
	}

	private float uiBgAlpha = 1.0f;

	public static float getUiBgAlpha() {
		return instance.uiBgAlpha;
	}

	/**
	 * Available (and mandatory) properties
	 */
	public static enum Prop {

		/** COLOR root background color */
		rootBackgroundColor("root.background.color"),

		/** INT pixel width of the border around the frame */
		frameBorderWidth("frame.border.width"),
		/** COLOR frame border color when focused */
		frameBorderFocusedColor("frame.border.focused.color"),
		/** COLOR frame border color when not focused */
		frameBorderUnfocusedColor("frame.border.unfocused.color"),
		/** COLOR frame titlebar color when focused */
		frameTitlebarFocusedColor("frame.titlebar.focused.color"),
		/** COLOR frame titlebar color when not focused */
		frameTitlebarUnfocusedColor("frame.titlebar.unfocused.color"),

		/** FLOAT frame opacity when moved */
		frameMovedAlpha("frame.moved.alpha"),
		/** FLOAT frame opacity when resized */
		frameResizedAlpha("frame.resized.alpha"),
		/** FLOAT frame opacity when not focused */
		frameUnfocusedAlpha("frame.unfocused.alpha"),

		/** BOOL true to enable dropped shadows around frames */
		frameShadowEnabled("frame.shadow.enabled"),
		/** FLOAT opacity of the frame shadow */
		frameShadowAlpha("frame.shadow.alpha"),
		/** INT pixel radius of the frame shadow */
		frameShadowRadius("frame.shadow.radius"),

		/** COLOR background color of the ui */
		uiBgColor("ui.bg.color"),
		/** FLOAT opacity of the background of the ui */
		uiBgAlpha("ui.bg.alpha");

		private String name = "";

		public String getName() {
			return this.name;
		}

		private Prop(String name) {
			this.name = name;
		}
	}

	/** singleton instance */
	private static Theme instance = null;

	/**
	 * Default constructor
	 * 
	 * @param path file path to the theme descriptor
	 * @throws BifstkException theme could not be opened
	 */
	private Theme(String path) throws BifstkException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new BifstkException("Could not find theme file", e);
		}

		Properties props = new Properties();
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			throw new BifstkException("Could not read theme file", e);
		}

		HashMap<Prop, String> readProperties = new HashMap<Prop, String>();

		try {
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				for (Prop p : Prop.values()) {
					if (p.getName().equals(key)) {
						readProperties.put(p, (String) entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			throw new BifstkException("Error parsing configuration file", e);
		}

		for (Prop p : Prop.values()) {
			if (!readProperties.containsKey(p)) {
				String message = "Property " + p + " (" + p.getName()
						+ ") is not defined in theme descriptor";
				throw new BifstkException(message);
			}
		}

		readProps(readProperties);
	}

	private void readProps(Map<Prop, String> props) throws BifstkException {
		for (Entry<Prop, String> prop : props.entrySet()) {
			String sval = prop.getValue();

			try {
				switch (prop.getKey()) {
				case rootBackgroundColor: {
					this.rootBackgroundColor = Color.parse(sval);
					break;
				}
				case frameBorderWidth: {
					this.frameBorderWidth = clampi(Integer.parseInt(sval),
							frameBorderWidthMin, frameBorderWidthMax);
					break;
				}
				case frameBorderFocusedColor: {
					this.frameBorderFocusedColor = Color.parse(sval);
					break;
				}
				case frameBorderUnfocusedColor: {
					this.frameBorderUnfocusedColor = Color.parse(sval);
					break;
				}
				case frameTitlebarFocusedColor: {
					this.frameTitlebarFocusedColor = Color.parse(sval);
					break;
				}
				case frameTitlebarUnfocusedColor: {
					this.frameTitlebarUnfocusedColor = Color.parse(sval);
					break;
				}
				case frameShadowEnabled: {
					this.frameShadowEnabled = Boolean.parseBoolean(sval);
					break;
				}
				case frameShadowAlpha: {
					this.frameShadowAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case frameShadowRadius: {
					this.frameShadowRadius = clampi(Integer.parseInt(sval),
							frameShadowRadiusMin, frameShadowRadiusMax);
					break;
				}
				case frameMovedAlpha: {
					this.frameMovedAlpha = clampf(Float.parseFloat(sval), 0.0f,
							1.0f);
					break;
				}
				case frameResizedAlpha: {
					this.frameResizedAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case frameUnfocusedAlpha: {
					this.frameUnfocusedAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case uiBgColor: {
					this.uiBgColor = Color.parse(sval);
					break;
				}
				case uiBgAlpha: {
					this.uiBgAlpha = clampf(Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				}
			} catch (Throwable t) {
				throw new BifstkException("Could not read value for "
						+ prop.getKey().getName(), t);
			}
		}
	}

	/**
	 * Clamp integer in specified range
	 * 
	 * @param val value to clamp
	 * @param min min value
	 * @param max max value
	 * @return a value comprised between min and max
	 */
	private static int clampi(int val, int min, int max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Clamp float in specified range
	 * 
	 * @param val value to clamp
	 * @param min min value
	 * @param max max value
	 * @return a value comprised between min and max
	 */
	private static float clampf(float val, float min, float max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Load a theme file
	 * 
	 * @param path file path to the theme descriptor
	 * @throws BifstkException theme could not be opened
	 */
	public static void load(String path) throws BifstkException {
		instance = new Theme(path);
		Logger.debug("Theme loaded from: " + path);
	}
}
