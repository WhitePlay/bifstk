package bifstk.gl;

import org.lwjgl.opengl.GL11;

public class Color {

	private float red = 1.0f;

	private float green = 1.0f;

	private float blue = 1.0f;

	private float alpha = 1.0f;

	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color GREY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);

	public Color() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	public Color(float r, float g, float b, float a) {
		r = Math.min(1.0f, Math.max(r, 0.0f));

		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	public void use() {
		GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
	}

}
