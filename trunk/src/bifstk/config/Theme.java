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

	private Color rootBackgroundModalColor = null;

	/**
	 * @return color of the mask displayed when a modal is shown
	 */
	public static Color getRootBackgroundModalColor() {
		return instance.rootBackgroundModalColor;
	}

	private float rootBackgroundModalAlpha;

	/**
	 * @return opacity of the mask displayed when a modal is shown
	 */
	public static float getRootBackgroundModalAlpha() {
		return instance.rootBackgroundModalAlpha;
	}

	private int windowBorderWidth;
	private int windowBorderWidthMin = 1, windowBorderWidthMax = 10;

	/**
	 * @return pixel width of the border around the window
	 */
	public static int getWindowBorderWidth() {
		return instance.windowBorderWidth;
	}

	private Color windowBorderFocusedColor = null;

	/**
	 * @return window border color when focused
	 */
	public static Color getWindowBorderFocusedColor() {
		return instance.windowBorderFocusedColor;
	}

	private Color windowBorderUnfocusedColor = null;

	/**
	 * @return window border color when not focused
	 */
	public static Color getWindowBorderUnfocusedColor() {
		return instance.windowBorderUnfocusedColor;
	}

	private boolean windowBorderRounded;

	/**
	 * @return true if window border corners should be rounded
	 */
	public static boolean isWindowBorderRounded() {
		return instance.windowBorderRounded;
	}

	private Color windowBorderOuterFocusedColor;

	/**
	 * @return the color of the 1px border of the window border when focused
	 */
	public static Color getWindowBorderOuterFocusedColor() {
		return instance.windowBorderOuterFocusedColor;
	}

	private Color windowBorderOuterUnfocusedColor;

	/**
	 * @return the color of the 1px border of the window border when not focused
	 */
	public static Color getWindowBorderOuterUnfocusedColor() {
		return instance.windowBorderOuterUnfocusedColor;
	}

	private Color windowTitlebarFocusedColor = null;

	/**
	 * @return window titlebar color when focused
	 */
	public static Color getWindowTitlebarFocusedColor() {
		return instance.windowTitlebarFocusedColor;
	}

	private Color windowTitlebarUnfocusedColor = null;

	/**
	 * @return window titlebar color when not focused
	 */
	public static Color getWindowTitlebarUnfocusedColor() {
		return instance.windowTitlebarUnfocusedColor;
	}

	private int areaBorderWidth;
	private static final int areaBorderMin = 1, areaBorderMax = 20;

	/**
	 * @return pixel width of the border around areas
	 */
	public static int getAreaBorderWidth() {
		return instance.areaBorderWidth;
	}

	private boolean areaBorderRounded;

	/**
	 * @return true if the area border is rounded
	 */
	public static boolean isAreaBorderRounded() {
		return instance.areaBorderRounded;
	}

	private Color areaFocusedColor = null;

	/**
	 * @return the focused color of the area
	 */
	public static Color getAreaFocusedColor() {
		return instance.areaFocusedColor;
	}

	private Color areaUnfocusedColor = null;

	/**
	 * @return the unfocused color of the area
	 */
	public static Color getAreaUnfocusedColor() {
		return instance.areaUnfocusedColor;
	}

	private Color areaBorderFocusedColor = null;

	/**
	 * @return the focused color of the outer border of the area
	 */
	public static Color getAreaBorderFocusedColor() {
		return instance.areaBorderFocusedColor;
	}

	private Color areaBorderUnfocusedColor = null;

	/**
	 * @return the unfocused color of the outer border of the area
	 */
	public static Color getAreaBorderUnfocusedColor() {
		return instance.areaBorderUnfocusedColor;
	}

	private float areaUiAlpha;

	/**
	 * @return the opacity of the area ui
	 */
	public static float getAreaUiAlpha() {
		return instance.areaUiAlpha;
	}

	private float areaUnfocusedAlpha;

	/**
	 * @return the opacity of an area when not focused
	 */
	public static float getAreaUnfocusedAlpha() {
		return instance.areaUnfocusedAlpha;
	}

	private float windowMovedAlpha;

	/**
	 * @return window opacity when moved
	 */
	public static float getWindowMovedAlpha() {
		return instance.windowMovedAlpha;
	}

	private float windowResizedAlpha;

	/**
	 * @return window opacity when resized
	 */
	public static float getWindowResizedAlpha() {
		return instance.windowResizedAlpha;
	}

	private float windowUnfocusedAlpha;

	/**
	 * @return window opacity when not focused
	 */
	public static float getWindowUnfocusedAlpha() {
		return instance.windowUnfocusedAlpha;
	}

	private boolean windowShadowEnabled;

	/**
	 * @return true to enable dropped shadows around windows
	 */
	public static boolean isWindowShadowEnabled() {
		return instance.windowShadowEnabled;
	}

	private float windowShadowAlpha;

	/**
	 * @return opacity of the window shadow
	 */
	public static float getWindowShadowAlpha() {
		return instance.windowShadowAlpha;
	}

	private int windowShadowRadius;
	private int windowShadowRadiusMin = 1, windowShadowRadiusMax = 20;

	/**
	 * @return pixel radius of the window shadow
	 */
	public static int getWindowShadowRadius() {
		return instance.windowShadowRadius;
	}

	private Color windowUiColor = null;

	/**
	 * @return color of the Window ui
	 */
	public static Color getWindowUiColor() {
		return instance.windowUiColor;
	}

	private float windowUiAlpha = 1.0f;

	/**
	 * @return opacity of a window UI
	 */
	public static float getWindowUiAlpha() {
		return instance.windowUiAlpha;
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

	private Color frameControlsCloseColor;

	/**
	 * @return frame close control color
	 */
	public static Color getFrameControlsCloseColor() {
		return instance.frameControlsCloseColor;
	}

	private Color frameControlsCloseHoverColor;

	/**
	 * @return close frame control color when hovered
	 */
	public static Color getFrameControlsCloseHoverColor() {
		return instance.frameControlsCloseHoverColor;
	}

	private Color frameControlsCloseClickColor;

	/**
	 * @return close frame control color when clicked
	 */
	public static Color getFrameControlsCloseClickColor() {
		return instance.frameControlsCloseClickColor;
	}

	private Color frameControlsMaximizeColor;

	/**
	 * @return frame Maximize control color
	 */
	public static Color getFrameControlsMaximizeColor() {
		return instance.frameControlsMaximizeColor;
	}

	private Color frameControlsMaximizeHoverColor;

	/**
	 * @return Maximize frame control color when hovered
	 */
	public static Color getFrameControlsMaximizeHoverColor() {
		return instance.frameControlsMaximizeHoverColor;
	}

	private Color frameControlsMaximizeClickColor;

	/**
	 * @return Maximize frame control color when clicked
	 */
	public static Color getFrameControlsMaximizeClickColor() {
		return instance.frameControlsMaximizeClickColor;
	}

	private Color uiButtonColor = null;

	/**
	 * @return the background color of a Button
	 */
	public static Color getUiButtonColor() {
		return instance.uiButtonColor;
	}

	private Color uiButtonHoverColor = null;

	/**
	 * @return the background color of a Button when hovered
	 */
	public static Color getUiButtonHoverColor() {
		return instance.uiButtonHoverColor;
	}

	private Color uiButtonClickColor = null;

	/**
	 * @return the background color of a Button when clicked
	 */
	public static Color getUiButtonClickColor() {
		return instance.uiButtonClickColor;
	}

	private Color uiButtonBorderColor = null;

	/**
	 * @return the border color of a Button
	 */
	public static Color getUiButtonBorderColor() {
		return instance.uiButtonBorderColor;
	}

	/**
	 * Available (and mandatory) properties
	 */
	public static enum Prop {

		/** COLOR root background color */
		rootBackgroundColor("root.background.color"),
		/** COLOR color superposed to the root bg when a modal frame is shown */
		rootBackgroundModalColor("root.background.modal.color"),
		/** FLOAT opacity of the color layered when a modal is shown */
		rootBackgroundModalAlpha("root.background.modal.alpha"),

		/** INT pixel width of the border around the window */
		windowBorderWidth("window.border.width"),
		/** COLOR window border color when focused */
		windowBorderFocusedColor("window.border.focused.color"),
		/** COLOR window border color when not focused */
		windowBorderUnfocusedColor("window.border.unfocused.color"),
		/** BOOL true for rounded window corners */
		windowBorderRounded("window.border.rounded"),
		/** COLOR color of the border of the window border */
		windowBorderOuterFocusedColor("window.border.outer.focused.color"),
		/** COLOR color of the border of the window border */
		windowBorderOuterUnfocusedColor("window.border.outer.unfocused.color"),
		/** COLOR window titlebar color when focused */
		windowTitlebarFocusedColor("window.titlebar.focused.color"),
		/** COLOR window titlebar color when not focused */
		windowTitlebarUnfocusedColor("window.titlebar.unfocused.color"),
		/** FLOAT window opacity when moved */
		windowMovedAlpha("window.moved.alpha"),
		/** FLOAT window opacity when resized */
		windowResizedAlpha("window.resized.alpha"),
		/** FLOAT window opacity when not focused */
		windowUnfocusedAlpha("window.unfocused.alpha"),
		/** BOOL true to enable dropped shadows around windows */
		windowShadowEnabled("window.shadow.enabled"),
		/** FLOAT opacity of the window shadow */
		windowShadowAlpha("window.shadow.alpha"),
		/** INT pixel radius of the window shadow */
		windowShadowRadius("window.shadow.radius"),
		/** COLOR base color of a Window ui */
		windowUiColor("window.ui.color"),
		/** FLOAT base opacity of the window ui */
		windowUiAlpha("window.ui.alpha"),

		/** INT pixel width of the border around the area */
		areaBorderWidth("area.border.width"),
		/** COLOR focused color of the area */
		areaFocusedColor("area.focused.color"),
		/** COLOR unfocused color of the area */
		areaUnfocusedColor("area.unfocused.color"),
		/** BOOL true if border corners should be rounded */
		areaBorderRounded("area.border.rounded"),
		/** COLOR focused color of the outer border */
		areaBorderFocusedColor("area.border.focused.color"),
		/** COLOR unfocused color of the outer border */
		areaBorderUnfocusedColor("area.border.unfocused.color"),
		/** FLOAT opacity of the area ui */
		areaUiAlpha("area.ui.alpha"),
		/** FLOAT opacity if of the area when not focused */
		areaUnfocusedAlpha("area.unfocused.alpha"),

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
		/** COLOR close frame control color */
		frameControlsCloseColor("frame.controls.close.color"),
		/** COLOR close frame control color when hovered */
		frameControlsCloseHoverColor("frame.controls.close.hover.color"),
		/** COLOR close frame control color when clicked */
		frameControlsCloseClickColor("frame.controls.close.click.color"),
		/** STRING path to the image for the maximize frame control */
		frameControlsMaximizeImage("frame.controls.maximize.image"),
		/** COLOR maximize frame control color */
		frameControlsMaximizeColor("frame.controls.maximize.color"),
		/** COLOR maximize frame control color when hovered */
		frameControlsMaximizeHoverColor("frame.controls.maximize.hover.color"),
		/** COLOR maximize frame control color when hovered */
		frameControlsMaximizeClickColor("frame.controls.maximize.click.color"),

		/** COLOR color of the ui button border */
		uiButtonBorderColor("ui.button.border.color"),
		/** COLOR color of the ui button background */
		uiButtonColor("ui.button.color"),
		/** COLOR color of the ui button background when focused */
		uiButtonHoverColor("ui.button.hover.color"),
		/** COLOR color of the ui button background when clicked */
		uiButtonClickColor("ui.button.click.color");

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
				case rootBackgroundModalColor: {
					this.rootBackgroundModalColor = Color.parse(sval);
					break;
				}
				case rootBackgroundModalAlpha: {
					this.rootBackgroundModalAlpha = clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowBorderWidth: {
					this.windowBorderWidth = clampi(Integer.parseInt(sval),
							windowBorderWidthMin, windowBorderWidthMax);
					break;
				}
				case windowBorderFocusedColor: {
					this.windowBorderFocusedColor = Color.parse(sval);
					break;
				}
				case windowBorderUnfocusedColor: {
					this.windowBorderUnfocusedColor = Color.parse(sval);
					break;
				}
				case windowBorderRounded: {
					this.windowBorderRounded = Boolean.parseBoolean(sval);
					break;
				}
				case windowBorderOuterFocusedColor: {
					this.windowBorderOuterFocusedColor = Color.parse(sval);
					break;
				}
				case windowBorderOuterUnfocusedColor: {
					this.windowBorderOuterUnfocusedColor = Color.parse(sval);
					break;
				}
				case windowTitlebarFocusedColor: {
					this.windowTitlebarFocusedColor = Color.parse(sval);
					break;
				}
				case windowTitlebarUnfocusedColor: {
					this.windowTitlebarUnfocusedColor = Color.parse(sval);
					break;
				}
				case windowShadowEnabled: {
					this.windowShadowEnabled = Boolean.parseBoolean(sval);
					break;
				}
				case windowShadowAlpha: {
					this.windowShadowAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case windowShadowRadius: {
					this.windowShadowRadius = clampi(Integer.parseInt(sval),
							windowShadowRadiusMin, windowShadowRadiusMax);
					break;
				}
				case windowMovedAlpha: {
					this.windowMovedAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case windowResizedAlpha: {
					this.windowResizedAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case windowUnfocusedAlpha: {
					this.windowUnfocusedAlpha = clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case windowUiColor: {
					this.windowUiColor = Color.parse(sval);
					break;
				}
				case windowUiAlpha: {
					this.windowUiAlpha = clampf(Float.parseFloat(sval), 0.0f,
							1.0f);
					break;
				}
				case areaBorderWidth: {
					this.areaBorderWidth = clampi(Integer.parseInt(sval),
							areaBorderMin, areaBorderMax);
					break;
				}
				case areaBorderRounded: {
					this.areaBorderRounded = Boolean.parseBoolean(sval);
					break;
				}
				case areaFocusedColor: {
					this.areaFocusedColor = Color.parse(sval);
					break;
				}
				case areaUnfocusedColor: {
					this.areaUnfocusedColor = Color.parse(sval);
					break;
				}
				case areaBorderFocusedColor: {
					this.areaBorderFocusedColor = Color.parse(sval);
					break;
				}
				case areaBorderUnfocusedColor: {
					this.areaBorderUnfocusedColor = Color.parse(sval);
					break;
				}
				case areaUiAlpha: {
					this.areaUiAlpha = clampf(Float.parseFloat(sval), 0.0f,
							1.0f);
					break;
				}
				case areaUnfocusedAlpha: {
					this.areaUnfocusedAlpha = clampf(Float.parseFloat(sval),
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
				case frameControlsCloseColor: {
					this.frameControlsCloseColor = Color.parse(sval);
					break;
				}
				case frameControlsCloseHoverColor: {
					this.frameControlsCloseHoverColor = Color.parse(sval);
					break;
				}
				case frameControlsCloseClickColor: {
					this.frameControlsCloseClickColor = Color.parse(sval);
					break;
				}
				case frameControlsMaximizeColor: {
					this.frameControlsMaximizeColor = Color.parse(sval);
					break;
				}
				case frameControlsMaximizeHoverColor: {
					this.frameControlsMaximizeHoverColor = Color.parse(sval);
					break;
				}
				case frameControlsMaximizeClickColor: {
					this.frameControlsMaximizeClickColor = Color.parse(sval);
					break;
				}
				case uiButtonColor: {
					this.uiButtonColor = Color.parse(sval);
					break;
				}
				case uiButtonHoverColor: {
					this.uiButtonHoverColor = Color.parse(sval);
					break;
				}
				case uiButtonClickColor: {
					this.uiButtonClickColor = Color.parse(sval);
					break;
				}
				case uiButtonBorderColor: {
					this.uiButtonBorderColor = Color.parse(sval);
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
