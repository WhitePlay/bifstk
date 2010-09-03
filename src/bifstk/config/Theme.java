package bifstk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.newdawn.slick.Image;

import bifstk.gl.Color;
import bifstk.util.BifstkException;
import bifstk.util.Logger;
import bifstk.wm.Frame.Controls;

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

	/**
	 * @return frame border color when focused
	 */
	public static Color getFrameBorderFocusedColor() {
		return instance.frameBorderFocusedColor;
	}

	private Color frameBorderUnfocusedColor = null;

	/**
	 * @return frame border color when not focused
	 */
	public static Color getFrameBorderUnfocusedColor() {
		return instance.frameBorderUnfocusedColor;
	}

	private Color frameTitlebarFocusedColor = null;

	/**
	 * @return frame titlebar color when focused
	 */
	public static Color getFrameTitlebarFocusedColor() {
		return instance.frameTitlebarFocusedColor;
	}

	private Color frameTitlebarUnfocusedColor = null;

	/**
	 * @return frame titlebar color when not focused
	 */
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

	private List<Controls> frameControlsOrder = null;

	/**
	 * @return title frame controls order
	 */
	public static List<Controls> getFrameControlsOrder() {
		return instance.frameControlsOrder;
	}

	private int frameControlsWidth;
	private int frameControlsWidthMin = 10, frameControlsWidthMax = 32;

	/**
	 * @return width of the frame controls
	 */
	public static int getFrameControlsWidth() {
		return instance.frameControlsWidth;
	}

	private int frameControlsHeight;
	private int frameControlsHeightMin = 10, frameControlsHeightMax = 32;

	/**
	 * @return height of the frame controls
	 */
	public static int getFrameControlsHeight() {
		return instance.frameControlsHeight;
	}

	private int frameControlsBorder;
	private int frameControlsBorderMin = 0, frameControlsBorderMax = 10;

	/**
	 * @return width of the frame controls
	 */
	public static int getFrameControlsBorder() {
		return instance.frameControlsBorder;
	}

	private Image frameControlCloseImage;

	/**
	 * @return the image for the close frame control
	 */
	public static Image getFrameControlCloseImage() {
		return instance.frameControlCloseImage;
	}

	private Image frameControlMaximizeImage;

	/**
	 * @return the image for the maximize frame control
	 */
	public static Image getFrameControlMaximizeImage() {
		return instance.frameControlMaximizeImage;
	}

	private Color uiBgColor = null;

	/**
	 * @return the background color of the ui
	 */
	public static Color getUiBgColor() {
		return instance.uiBgColor;
	}

	private float uiBgAlpha = 1.0f;

	/**
	 * @return opacity of the background of the ui
	 */
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

		/** List<bifstk.wm.Frame.Controls> title frame controls order */
		frameControlsOrder("frame.controls.order"),
		/** INT width of the frame controls */
		frameControlsWidth("frame.controls.width"),
		/** INT height of the frame controls */
		frameControlsHeight("frame.controls.height"),
		/** INT spacing border between frame controls */
		frameControlsBorder("frame.controls.border"),
		/** STRING path to the image for the close frame control */
		frameControlsCloseImage("frame.controls.close.image"),
		/** STRING path to the image for the maximize frame control */
		frameControlsMaximizeImage("frame.controls.maximize.image"),

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

	/** path to this file */
	private String path = "";

	/**
	 * Default constructor
	 * 
	 * @param path file path to the theme descriptor
	 * @throws BifstkException theme could not be opened
	 */
	private Theme(String path) throws BifstkException {
		File f = null;
		FileInputStream in = null;
		try {
			f = new File(path);
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new BifstkException("Could not find theme file", e);
		}
		this.path = f.getParent();

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
				case frameControlsOrder: {
					Controls[] controls = Controls.values();
					this.frameControlsOrder = new ArrayList<Controls>(
							controls.length);
					String[] str = sval.trim().split(" ");
					if (str.length != controls.length) {
						throw new BifstkException("Expected " + controls.length
								+ " controls in string: " + sval);
					}
					for (int i = 0; i < controls.length; i++) {
						for (int j = 0; j < controls.length; j++) {
							if (str[i].equalsIgnoreCase(controls[j].getName())) {
								if (this.frameControlsOrder
										.contains(controls[j])) {
									throw new BifstkException(
											"Multiple definitions of frame control: "
													+ controls[j].getName());
								} else {
									this.frameControlsOrder.add(controls[j]);
								}
							}
						}
					}
					for (int i = 0; i < controls.length; i++) {
						if (!this.frameControlsOrder.contains(controls[i])) {
							throw new BifstkException("Missing frame control: "
									+ controls[i].getName());
						}
					}
					break;
				}
				case frameControlsWidth: {
					this.frameControlsWidth = clampi(Integer.parseInt(sval),
							this.frameControlsWidthMin,
							this.frameControlsWidthMax);
					break;
				}
				case frameControlsHeight: {
					this.frameControlsHeight = clampi(Integer.parseInt(sval),
							this.frameControlsHeightMin,
							this.frameControlsHeightMax);
					break;
				}
				case frameControlsBorder: {
					this.frameControlsBorder = clampi(Integer.parseInt(sval),
							this.frameControlsBorderMin,
							this.frameControlsBorderMax);
					break;
				}
				case frameControlsCloseImage: {
					this.frameControlCloseImage = new Image(this.path + "/"
							+ sval);
					break;
				}
				case frameControlsMaximizeImage: {
					this.frameControlMaximizeImage = new Image(this.path + "/"
							+ sval);
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
