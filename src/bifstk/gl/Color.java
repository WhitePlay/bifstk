package bifstk.gl;

import org.lwjgl.opengl.GL11;

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
		}
		return WHITE;
	}

}
