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

	public Color rootBackgroundColor = null;
	public Color rootBackgroundModalColor = null;
	public float rootBackgroundModalAlpha;

	public List<Controls> frameControlsOrder = null;
	public int frameControlsWidth;
	private int frameControlsWidthMin = 10, frameControlsWidthMax = 32;
	public int frameControlsHeight;
	private int frameControlsHeightMin = 10, frameControlsHeightMax = 32;
	public int frameControlsBorder;
	private int frameControlsBorderMin = 0, frameControlsBorderMax = 10;
	public Color frameControlsCloseColor;
	public Color frameControlsCloseHoverColor;
	public Color frameControlsCloseClickColor;
	public Color frameControlsCloseUnfocusedColor;
	public Color frameControlsMaximizeColor;
	public Color frameControlsMaximizeHoverColor;
	public Color frameControlsMaximizeClickColor;
	public Color frameControlsMaximizeUnfocusedColor;

	public int windowBorderWidth;
	private int windowBorderWidthMin = 1, windowBorderWidthMax = 10;
	public Color windowBorderFocusedColor = null;
	public Color windowBorderUnfocusedColor = null;
	public Color windowBorderOuterFocusedColor;
	public Color windowBorderOuterUnfocusedColor;
	public Color windowTitlebarFocusedColor = null;
	public Color windowTitlebarUnfocusedColor = null;
	public float windowMovedAlpha;
	public float windowResizedAlpha;
	public float windowUnfocusedAlpha;
	public boolean windowShadowEnabled;
	public float windowShadowAlpha;
	public Color windowFocusedColor = null;
	public Color windowUnfocusedColor = null;
	public float windowFocusedAlpha = 1.0f;
	public Color windowShadowFocusedColor;
	public Color windowShadowUnfocusedColor;

	public int areaBorderWidth;
	private static final int areaBorderMin = 1, areaBorderMax = 20;
	public Color areaFocusedColor = null;
	public Color areaUnfocusedColor = null;
	public Color areaBorderFocusedColor = null;
	public Color areaBorderUnfocusedColor = null;
	public float areaFocusedAlpha;
	public float areaUnfocusedAlpha;

	public Color uiButtonColor = null;
	public Color uiButtonHoverColor = null;
	public Color uiButtonClickColor = null;
	public Color uiBorderColor = null;
	public Color uiEntryColor;
	public Color uiEntryFontColor;
	public Color uiFontColor;
	public Color uiTabFocusedHighlightColor;
	public Color uiTabUnfocusedBorderColor;
	public Color uiTabUnfocusedFontColor;
	public Color uiTabUnfocusedBackgroundColor;
	public Color uiTabUnfocusedHighlightColor;

	/** singleton instance */
	private static Theme instance = null;

	/** path to this file */
	private String path = "";

	/**
	 * @return the singleton instance if {@link #load(String)} has been called
	 */
	public static Theme get() {
		return instance;
	}

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
				case texturesImage: {
					TextureLoader.load(this.path + "/" + sval);
					break;
				}
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
				case frameControlsCloseUnfocusedColor: {
					this.frameControlsCloseUnfocusedColor = Color.parse(sval);
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
				case frameControlsMaximizeUnfocusedColor: {
					this.frameControlsMaximizeUnfocusedColor = Color
							.parse(sval);
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
				case uiBorderColor: {
					this.uiBorderColor = Color.parse(sval);
					break;
				}
				case uiEntryColor: {
					this.uiEntryColor = Color.parse(sval);
					break;
				}
				case uiEntryFontColor: {
					this.uiEntryFontColor = Color.parse(sval);
				}
				case uiFontColor: {
					this.uiFontColor = Color.parse(sval);
					break;
				}
				case uiTabFocusedHighlightColor: {
					this.uiTabFocusedHighlightColor = Color.parse(sval);
					break;
				}
				case uiTabUnfocusedBorderColor: {
					this.uiTabUnfocusedBorderColor = Color.parse(sval);
					break;
				}
				case uiTabUnfocusedFontColor: {
					this.uiTabUnfocusedFontColor = Color.parse(sval);
					break;
				}
				case uiTabUnfocusedBackgroundColor: {
					this.uiTabUnfocusedBackgroundColor = Color.parse(sval);
					break;
				}
				case uiTabUnfocusedHighlightColor: {
					this.uiTabUnfocusedHighlightColor = Color.parse(sval);
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
