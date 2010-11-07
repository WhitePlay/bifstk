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
import bifstk.gl.Util;
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
		check();
		return instance.rootBackgroundColor;
	}

	private Color rootBackgroundModalColor = null;

	/**
	 * @return color of the mask displayed when a modal is shown
	 */
	public static Color getRootBackgroundModalColor() {
		check();
		return instance.rootBackgroundModalColor;
	}

	private float rootBackgroundModalAlpha;

	/**
	 * @return opacity of the mask displayed when a modal is shown
	 */
	public static float getRootBackgroundModalAlpha() {
		check();
		return instance.rootBackgroundModalAlpha;
	}

	private int windowBorderWidth;
	private int windowBorderWidthMin = 1, windowBorderWidthMax = 10;

	/**
	 * @return pixel width of the border around the window
	 */
	public static int getWindowBorderWidth() {
		check();
		return instance.windowBorderWidth;
	}

	private Color windowBorderFocusedColor = null;

	/**
	 * @return window border color when focused
	 */
	public static Color getWindowBorderFocusedColor() {
		check();
		return instance.windowBorderFocusedColor;
	}

	private Color windowBorderUnfocusedColor = null;

	/**
	 * @return window border color when not focused
	 */
	public static Color getWindowBorderUnfocusedColor() {
		check();
		return instance.windowBorderUnfocusedColor;
	}

	private Color windowBorderOuterFocusedColor;

	/**
	 * @return the color of the 1px border of the window border when focused
	 */
	public static Color getWindowBorderOuterFocusedColor() {
		check();
		return instance.windowBorderOuterFocusedColor;
	}

	private Color windowBorderOuterUnfocusedColor;

	/**
	 * @return the color of the 1px border of the window border when not focused
	 */
	public static Color getWindowBorderOuterUnfocusedColor() {
		check();
		return instance.windowBorderOuterUnfocusedColor;
	}

	private Color windowTitlebarFocusedColor = null;

	/**
	 * @return window titlebar color when focused
	 */
	public static Color getWindowTitlebarFocusedColor() {
		check();
		return instance.windowTitlebarFocusedColor;
	}

	private Color windowTitlebarUnfocusedColor = null;

	/**
	 * @return window titlebar color when not focused
	 */
	public static Color getWindowTitlebarUnfocusedColor() {
		check();
		return instance.windowTitlebarUnfocusedColor;
	}

	private int areaBorderWidth;
	private static final int areaBorderMin = 1, areaBorderMax = 20;

	/**
	 * @return pixel width of the border around areas
	 */
	public static int getAreaBorderWidth() {
		check();
		return instance.areaBorderWidth;
	}

	private Color areaFocusedColor = null;

	/**
	 * @return the focused color of the area
	 */
	public static Color getAreaFocusedColor() {
		check();
		return instance.areaFocusedColor;
	}

	private Color areaUnfocusedColor = null;

	/**
	 * @return the unfocused color of the area
	 */
	public static Color getAreaUnfocusedColor() {
		check();
		return instance.areaUnfocusedColor;
	}

	private Color areaBorderFocusedColor = null;

	/**
	 * @return the focused color of the outer border of the area
	 */
	public static Color getAreaBorderFocusedColor() {
		check();
		return instance.areaBorderFocusedColor;
	}

	private Color areaBorderUnfocusedColor = null;

	/**
	 * @return the unfocused color of the outer border of the area
	 */
	public static Color getAreaBorderUnfocusedColor() {
		check();
		return instance.areaBorderUnfocusedColor;
	}

	private float areaFocusedAlpha;

	/**
	 * @return the opacity of the area ui when focused
	 */
	public static float getAreaFocusedAlpha() {
		check();
		return instance.areaFocusedAlpha;
	}

	private float areaUnfocusedAlpha;

	/**
	 * @return the opacity of an area when not focused
	 */
	public static float getAreaUnfocusedAlpha() {
		check();
		return instance.areaUnfocusedAlpha;
	}

	private float windowMovedAlpha;

	/**
	 * @return window opacity when moved
	 */
	public static float getWindowMovedAlpha() {
		check();
		return instance.windowMovedAlpha;
	}

	private float windowResizedAlpha;

	/**
	 * @return window opacity when resized
	 */
	public static float getWindowResizedAlpha() {
		check();
		return instance.windowResizedAlpha;
	}

	private float windowUnfocusedAlpha;

	/**
	 * @return window opacity when not focused
	 */
	public static float getWindowUnfocusedAlpha() {
		check();
		return instance.windowUnfocusedAlpha;
	}

	private boolean windowShadowEnabled;

	/**
	 * @return true to enable dropped shadows around windows
	 */
	public static boolean isWindowShadowEnabled() {
		check();
		return instance.windowShadowEnabled;
	}

	private float windowShadowAlpha;

	/**
	 * @return opacity of the window shadow
	 */
	public static float getWindowShadowAlpha() {
		check();
		return instance.windowShadowAlpha;
	}

	private int windowShadowRadius;
	private int windowShadowRadiusMin = 1, windowShadowRadiusMax = 20;

	/**
	 * @return pixel radius of the window shadow
	 */
	public static int getWindowShadowRadius() {
		check();
		return instance.windowShadowRadius;
	}

	private Color windowFocusedColor = null;

	/**
	 * @return color of the Window ui
	 */
	public static Color getWindowFocusedColor() {
		check();
		return instance.windowFocusedColor;
	}

	private Color windowUnfocusedColor = null;

	/**
	 * @return color of the Window ui when not focused
	 */
	public static Color getWindowUnfocusedColor() {
		check();
		return instance.windowUnfocusedColor;
	}

	private float windowFocusedAlpha = 1.0f;

	/**
	 * @return opacity of a window UI when focused
	 */
	public static float getWindowFocusedAlpha() {
		check();
		return instance.windowFocusedAlpha;
	}

	private Color windowTitlebarFocusedFontColor;

	/**
	 * @return the font color of the Window titlebar when focused
	 */
	public static Color getWindowTitlebarFocusedFontColor() {
		return instance.windowTitlebarFocusedFontColor;
	}

	private Color windowTitlebarUnfocusedFontColor;

	/**
	 * @return the font color of the Window titlebar when not focused
	 */
	public static Color getWindowTitlebarUnfocusedFontColor() {
		return instance.windowTitlebarUnfocusedFontColor;
	}

	private Color windowShadowFocusedColor;

	/**
	 * @return the color of Window shadows when focused
	 */
	public static Color getWindowShadowFocusedColor() {
		return instance.windowShadowFocusedColor;
	}

	private Color windowShadowUnfocusedColor;

	/**
	 * @return the color of Window shadows when not focused
	 */
	public static Color getWindowShadowUnfocusedColor() {
		return instance.windowShadowUnfocusedColor;
	}

	private List<Controls> frameControlsOrder = null;

	/**
	 * @return title frame controls order
	 */
	public static List<Controls> getFrameControlsOrder() {
		check();
		return instance.frameControlsOrder;
	}

	private int frameControlsWidth;
	private int frameControlsWidthMin = 10, frameControlsWidthMax = 32;

	/**
	 * @return width of the frame controls
	 */
	public static int getFrameControlsWidth() {
		check();
		return instance.frameControlsWidth;
	}

	private int frameControlsHeight;
	private int frameControlsHeightMin = 10, frameControlsHeightMax = 32;

	/**
	 * @return height of the frame controls
	 */
	public static int getFrameControlsHeight() {
		check();
		return instance.frameControlsHeight;
	}

	private int frameControlsBorder;
	private int frameControlsBorderMin = 0, frameControlsBorderMax = 10;

	/**
	 * @return width of the frame controls
	 */
	public static int getFrameControlsBorder() {
		check();
		return instance.frameControlsBorder;
	}

	private Image frameControlCloseImage;

	/**
	 * @return the image for the close frame control
	 */
	public static Image getFrameControlCloseImage() {
		check();
		return instance.frameControlCloseImage;
	}

	private Image frameControlMaximizeImage;

	/**
	 * @return the image for the maximize frame control
	 */
	public static Image getFrameControlMaximizeImage() {
		check();
		return instance.frameControlMaximizeImage;
	}

	private Color frameControlsCloseColor;

	/**
	 * @return frame close control color
	 */
	public static Color getFrameControlsCloseColor() {
		check();
		return instance.frameControlsCloseColor;
	}

	private Color frameControlsCloseHoverColor;

	/**
	 * @return close frame control color when hovered
	 */
	public static Color getFrameControlsCloseHoverColor() {
		check();
		return instance.frameControlsCloseHoverColor;
	}

	private Color frameControlsCloseClickColor;

	/**
	 * @return close frame control color when clicked
	 */
	public static Color getFrameControlsCloseClickColor() {
		check();
		return instance.frameControlsCloseClickColor;
	}

	private Color frameControlsMaximizeColor;

	/**
	 * @return frame Maximize control color
	 */
	public static Color getFrameControlsMaximizeColor() {
		check();
		return instance.frameControlsMaximizeColor;
	}

	private Color frameControlsMaximizeHoverColor;

	/**
	 * @return Maximize frame control color when hovered
	 */
	public static Color getFrameControlsMaximizeHoverColor() {
		check();
		return instance.frameControlsMaximizeHoverColor;
	}

	private Color frameControlsMaximizeClickColor;

	/**
	 * @return Maximize frame control color when clicked
	 */
	public static Color getFrameControlsMaximizeClickColor() {
		check();
		return instance.frameControlsMaximizeClickColor;
	}

	private Color uiButtonColor = null;

	/**
	 * @return the background color of a Button
	 */
	public static Color getUiButtonColor() {
		check();
		return instance.uiButtonColor;
	}

	private Color uiButtonHoverColor = null;

	/**
	 * @return the background color of a Button when hovered
	 */
	public static Color getUiButtonHoverColor() {
		check();
		return instance.uiButtonHoverColor;
	}

	private Color uiButtonClickColor = null;

	/**
	 * @return the background color of a Button when clicked
	 */
	public static Color getUiButtonClickColor() {
		check();
		return instance.uiButtonClickColor;
	}

	private Color uiButtonBorderColor = null;

	/**
	 * @return the border color of a Button
	 */
	public static Color getUiButtonBorderColor() {
		check();
		return instance.uiButtonBorderColor;
	}

	private Color uiFontColor;

	/**
	 * @return base color of the ui font
	 */
	public static Color getUiFontColor() {
		return instance.uiFontColor;
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

		HashMap<ThemeProperty, String> readProperties = new HashMap<ThemeProperty, String>();

		try {
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				for (ThemeProperty p : ThemeProperty.values()) {
					if (p.getName().equals(key)) {
						readProperties.put(p, (String) entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			throw new BifstkException("Error parsing configuration file", e);
		}

		for (ThemeProperty p : ThemeProperty.values()) {
			if (!readProperties.containsKey(p)) {
				String message = "Property " + p + " (" + p.getName()
						+ ") is not defined in theme descriptor";
				throw new BifstkException(message);
			}
		}

		readProps(readProperties);
	}

	private void readProps(Map<ThemeProperty, String> props)
			throws BifstkException {
		for (Entry<ThemeProperty, String> prop : props.entrySet()) {
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
					this.rootBackgroundModalAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowBorderWidth: {
					this.windowBorderWidth = Util.clampi(
							Integer.parseInt(sval), windowBorderWidthMin,
							windowBorderWidthMax);
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
					this.windowShadowAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowShadowRadius: {
					this.windowShadowRadius = Util.clampi(
							Integer.parseInt(sval), windowShadowRadiusMin,
							windowShadowRadiusMax);
					break;
				}
				case windowMovedAlpha: {
					this.windowMovedAlpha = Util.clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case windowResizedAlpha: {
					this.windowResizedAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowUnfocusedAlpha: {
					this.windowUnfocusedAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowFocusedColor: {
					this.windowFocusedColor = Color.parse(sval);
					break;
				}
				case windowUnfocusedColor: {
					this.windowUnfocusedColor = Color.parse(sval);
					break;
				}
				case windowFocusedAlpha: {
					this.windowFocusedAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
					break;
				}
				case windowTitlebarFocusedFontColor: {
					this.windowTitlebarFocusedFontColor = Color.parse(sval);
					break;
				}
				case windowTitlebarUnfocusedFontColor: {
					this.windowTitlebarUnfocusedFontColor = Color.parse(sval);
					break;
				}
				case windowShadowFocusedColor: {
					this.windowShadowFocusedColor = Color.parse(sval);
					break;
				}
				case windowShadowUnfocusedColor: {
					this.windowShadowUnfocusedColor = Color.parse(sval);
					break;
				}
				case areaBorderWidth: {
					this.areaBorderWidth = Util.clampi(Integer.parseInt(sval),
							areaBorderMin, areaBorderMax);
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
				case areaFocusedAlpha: {
					this.areaFocusedAlpha = Util.clampf(Float.parseFloat(sval),
							0.0f, 1.0f);
					break;
				}
				case areaUnfocusedAlpha: {
					this.areaUnfocusedAlpha = Util.clampf(
							Float.parseFloat(sval), 0.0f, 1.0f);
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
					this.frameControlsWidth = Util.clampi(
							Integer.parseInt(sval), this.frameControlsWidthMin,
							this.frameControlsWidthMax);
					break;
				}
				case frameControlsHeight: {
					this.frameControlsHeight = Util.clampi(
							Integer.parseInt(sval),
							this.frameControlsHeightMin,
							this.frameControlsHeightMax);
					break;
				}
				case frameControlsBorder: {
					this.frameControlsBorder = Util.clampi(
							Integer.parseInt(sval),
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
				case uiFontColor: {
					this.uiFontColor = Color.parse(sval);
					break;
				}
				}
			} catch (Throwable t) {
				throw new BifstkException("Could not read value for "
						+ prop.getKey().getName(), t);
			}
		}
	}

	private static void check() throws IllegalStateException {
		if (instance == null) {
			throw new IllegalStateException("Config has not been loaded");
		}
	}

	/**
	 * Load a theme file
	 * <p>
	 * Will create the singleton instance and enable access to property values
	 * through the appropriate public accessor
	 * 
	 * @param path file path to the theme descriptor
	 * @throws BifstkException theme could not be opened
	 */
	public static void load(String path) throws BifstkException {
		instance = new Theme(path);
		Logger.debug("Theme loaded from: " + path);
	}
}
