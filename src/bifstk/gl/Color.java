package bifstk.gl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

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
	 */
	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	/**
	 * Default constructor
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
	 * Bind this color in the current GL context
	 */
	public void use() {
		GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
	}

	/**
	 * Bind this color in the current GL context, multiplying alpha with a
	 * custom value
	 */
	public void use(float alpha) {
		GL11.glColor4f(this.red, this.green, this.blue, this.alpha * alpha);
	}

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
}
