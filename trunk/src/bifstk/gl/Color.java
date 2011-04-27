package bifstk.gl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bifstk.util.Logger;

public class Color {

	private float red = 1.0f;

	private float green = 1.0f;

	private float blue = 1.0f;

	private float alpha = 1.0f;

	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color LIGHT_GRAY = new Color(0.75f, 0.75f, 0.75f, 1.0f);
	public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
	public static final Color DARK_GRAY = new Color(0.25f, 0.25f, 0.25f, 1.0f);
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Color TRANSP_WHITE = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	public static final Color TRANSP_BLACK = new Color(0.0f, 0.0f, 0.0f, 0.0f);

	public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color LIGHT_RED = new Color(1.0f, 0.5f, 0.5f, 1.0f);
	public static final Color DARK_RED = new Color(0.5f, 0.0f, 0.0f, 1.0f);

	public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color LIGHT_GREEN = new Color(0.5f, 1.0f, 0.5f, 1.0f);
	public static final Color DARK_GREEN = new Color(0.0f, 0.5f, 0.0f, 1.0f);

	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
	public static final Color LIGHT_BLUE = new Color(0.5f, 0.5f, 1.0f, 1.0f);
	public static final Color DARK_BLUE = new Color(0.0f, 0.0f, 0.5f, 1.0f);

	/** regexp that parses [0.0, 1.0] colors from strings */
	private final static String fMatcher = "([01](?:[.][0-9]+)?) ([01](?:[.][0-9]+)?) ([01](?:[.][0-9]+)?)";
	/** compiled regexp for [0.0, 1.0] */
	private static Pattern fPattern = Pattern.compile(fMatcher);

	/** regexp that parses [0, 255] colors from strings */
	private final static String iMatcher = "([0-9]{1,2}|1[0-9]{2}|2[0-4][0-9]|25[0-5]) ([0-9]{1,2}|1[0-9]{2}|2[0-4][0-9]|25[0-5]) ([0-9]{1,2}|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
	/** compiled regexp for [0, 255] */
	private static Pattern iPattern = Pattern.compile(iMatcher);

	/**
	 * Default constructor
	 */
	public Color() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Default constructor
	 * 
	 * @param r [0.0, 1.0]
	 * @param g [0.0, 1.0]
	 * @param b [0.0, 1.0]
	 */
	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	/**
	 * Default constructor
	 *
	 * @param r [0.0, 1.0]
	 * @param g [0.0, 1.0]
	 * @param b [0.0, 1.0]
	 * @param a [0.0, 1.0]
	 */
	public Color(float r, float g, float b, float a) {
		r = Math.min(1.0f, Math.max(r, 0.0f));
		g = Math.min(1.0f, Math.max(g, 0.0f));
		b = Math.min(1.0f, Math.max(b, 0.0f));
		a = Math.min(1.0f, Math.max(a, 0.0f));

		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	/**
	 * @return this color as a float array
	 */
	public float[] toArray() {
		return toArray(1, 1.0f);
	}

	/**
	 * @param alpha multiply alpha
	 * @return this color as a float array
	 */
	public float[] toArray(float alpha) {
		return toArray(1, alpha);
	}

	/**
	 * @param num number of consecutive repetitions
	 * @return this color as a float array
	 */
	public float[] toArray(int num) {
		return toArray(num, 1.0f);
	}

	/**
	 * @param num number of consecutive repetitions
	 * @param alpha multiply alpha
	 * @return num consecutive repetitions of this color's elements as a float
	 *         array
	 */
	public float[] toArray(int num, float alpha) {
		if (num < 1) {
			throw new IllegalArgumentException("num must be > 0");
		}
		float[] res = new float[num * 4];
		for (int i = 0; i < num; i++) {
			res[4 * i] = this.red;
			res[4 * i + 1] = this.green;
			res[4 * i + 2] = this.blue;
			res[4 * i + 3] = this.alpha * alpha;
		}
		return res;
	}

	/**
	 * Fill an array with this Color's elements
	 * 
	 * @param array the array to fill
	 * @param beginIndex where to begin the filling
	 * @param endIndex where to stop
	 * @param alpha alpha multiplier
	 */
	public void fillArray(float[] array, int beginIndex, int endIndex,
			float alpha) {
		if (beginIndex >= endIndex || beginIndex < 0 || endIndex > array.length) {
			throw new IllegalArgumentException("Messed up indexes");
		}
		if (array.length % 4 != 0 || beginIndex % 4 != 0 || endIndex % 4 != 0) {
			throw new IllegalArgumentException(
					"array length and indexes must be multiples of 4");
		}
		for (int i = beginIndex; i < endIndex; i += 4) {
			array[i] = this.red;
			array[i + 1] = this.green;
			array[i + 2] = this.blue;
			array[i + 3] = this.alpha * alpha;
		}
	}

	/**
	 * @param target a Color to blend with this
	 * @param factor blending factor: 0.0 will return this, 1.0 will return
	 *            target
	 * @return a new Color created by blending each component of this and target
	 */
	public Color blend(Color target, float factor) {
		factor = Util.clampf(factor, 0.0f, 1.0f);
		float invFactor = 1.0f - factor;
		return new Color(this.red * factor + target.red * invFactor, //
				this.green * factor + target.green * invFactor, //
				this.blue * factor + target.blue * invFactor, //
				this.alpha * factor + target.alpha * invFactor);
	}

	/**
	 * @param target a Color to blend with this
	 * @param factor blending factor: 0.0 will return this, 1.0 will return
	 *            target
	 * @param alpha multiply alpha by this factor after blending
	 * @return a new Color created by blending each component of this and target
	 */
	public Color blend(Color target, float factor, float alpha) {
		factor = Util.clampf(factor, 0.0f, 1.0f);
		float invFactor = 1.0f - factor;
		return new Color(this.red * factor + target.red * invFactor, this.green
				* factor + target.green * invFactor, this.blue * factor
				+ target.blue * invFactor, alpha
				* (this.alpha * factor + target.alpha * invFactor));
	}

	/**
	 * Create a new Color object which components are this + param
	 * @param rgb value to add to each component in range [-1.0f, 1.0f]
	 * @return a new Color Object reflecting this + parameters
	 */
	public Color add(float[] rgb) {
		if (rgb.length != 3)
			throw new IllegalArgumentException(
					"Expected 3 floats for RGB components");
		return new Color(this.red + rgb[0], this.green + rgb[1], this.blue
				+ rgb[2], this.alpha);
	}

	/**
	 * A Color can be represented as a String in 3 forms:
	 * <ul><li>common color names, ie 'white', 'red'
	 * <li>3 ints in range [0,255], ie '255 0 127'
	 * <li>3 floats in range [0.0,1.0], ie '1.0 0 0.5'
	 * </ul>
	 * 
	 * @param str a textual color representation
	 * @return the color represented by the string argument, or white if parsing failed
	 */
	public static Color parse(String str) {
		if (str.equalsIgnoreCase("white")) {
			return WHITE;
		} else if (str.equalsIgnoreCase("gray")) {
			return GRAY;
		} else if (str.equalsIgnoreCase("lightgray")) {
			return LIGHT_GRAY;
		} else if (str.equalsIgnoreCase("darkgray")) {
			return DARK_GRAY;
		} else if (str.equalsIgnoreCase("black")) {
			return BLACK;
		} else if (str.equalsIgnoreCase("red")) {
			return RED;
		} else if (str.equalsIgnoreCase("lightred")) {
			return LIGHT_RED;
		} else if (str.equalsIgnoreCase("darkred")) {
			return DARK_RED;
		} else if (str.equalsIgnoreCase("green")) {
			return GREEN;
		} else if (str.equalsIgnoreCase("lightgreen")) {
			return LIGHT_GREEN;
		} else if (str.equalsIgnoreCase("darkgreen")) {
			return DARK_GREEN;
		} else if (str.equalsIgnoreCase("blue")) {
			return BLUE;
		} else if (str.equalsIgnoreCase("lightblue")) {
			return LIGHT_BLUE;
		} else if (str.equalsIgnoreCase("darkblue")) {
			return DARK_BLUE;
		} else {
			Matcher mat = fPattern.matcher(str.trim());
			if (mat.matches() && mat.groupCount() == 3) {
				float r = Float.parseFloat(mat.group(1));
				float g = Float.parseFloat(mat.group(2));
				float b = Float.parseFloat(mat.group(3));
				return new Color(r, g, b);
			}
			mat = iPattern.matcher(str.trim());
			if (mat.matches() && mat.groupCount() == 3) {
				int r = Integer.parseInt(mat.group(1));
				int g = Integer.parseInt(mat.group(2));
				int b = Integer.parseInt(mat.group(3));
				return new Color(r / 255.0f, g / 255.0f, b / 255.0f);
			}
		}
		Logger.warn("Could not read color: " + str);
		return WHITE;
	}

	public String toString() {
		return "[r=" + this.red + ",g=" + this.green + ",b=" + this.blue + "]";
	}
}
