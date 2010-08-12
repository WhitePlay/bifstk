package bifstk.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import bifstk.BifstkException;

/**
 * Configurable UI properties
 * <p>
 * Properties are held statically after being loaded
 * 
 */
public class Theme {

	private int frameBorderWidth;
	private int frameBorderWidthMin = 1, frameBorderWidthMax = 10;

	/**
	 * @return pixel width of the border around the frame
	 */
	public static int getFrameBorderWidth() {
		return instance.frameBorderWidth;
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

	/**
	 * Available (and mandatory) properties
	 */
	public static enum Prop {

		/** INT pixel width of the border around the frame */
		frameBorderWidth("frame.border.width"),

		/** FLOAT frame opacity when moved */
		frameMovedAlpha("frame.moved.alpha"),
		/** FLOAT frame opacity when resized */
		frameResizedAlpha("frame.resized.alpha"),

		/** BOOL true to enable dropped shadows around frames */
		frameShadowEnabled("frame.shadow.enabled"),
		/** FLOAT opacity of the frame shadow */
		frameShadowAlpha("frame.shadow.alpha"),
		/** INT pixel radius of the frame shadow */
		frameShadowRadius("frame.shadow.radius");

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
				case frameBorderWidth: {
					this.frameBorderWidth = Math.max(
							this.frameBorderWidthMin,
							Math.min(this.frameBorderWidthMax,
									Integer.parseInt(sval)));
					break;
				}
				case frameShadowEnabled: {
					this.frameShadowEnabled = Boolean.parseBoolean(sval);
					break;
				}
				case frameShadowAlpha: {
					this.frameShadowAlpha = Math.max(0.0f,
							Math.min(1.0f, Float.parseFloat(sval)));
					break;
				}
				case frameShadowRadius: {
					this.frameShadowRadius = Math.min(
							this.frameShadowRadiusMax,
							Math.max(this.frameShadowRadiusMin,
									Integer.parseInt(sval)));
					break;
				}
				case frameMovedAlpha: {
					this.frameMovedAlpha = Math.max(0.0f,
							Math.min(1.0f, Float.parseFloat(sval)));
					break;

				}
				case frameResizedAlpha: {
					this.frameResizedAlpha = Math.max(0.0f,
							Math.min(1.0f, Float.parseFloat(sval)));
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
	 * 
	 * @param path file path to the theme descriptor
	 * @throws BifstkException theme could not be opened
	 */
	public static void load(String path) throws BifstkException {
		instance = new Theme(path);
	}
}
