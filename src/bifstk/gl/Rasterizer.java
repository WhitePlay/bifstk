package bifstk.gl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import bifstk.config.Config;

/**
 * 2D drawing operations
 * 
 */
public abstract class Rasterizer {

	/** default texture coord for 2D Quads texture rendering */
	protected float[] defaultCoords = {
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
	};

	/** magic numbers; some people advertise using 0.375, this works for me */
	protected final float raster_off_1 = 0.2f, raster_off_2 = 0.2f;

	/** singleton instance */
	private static Rasterizer instance = null;

	/**
	 * first element of the queue defines the region of the viewport that can
	 * actually be drawn
	 */
	private static LinkedList<Rectangle> scissors = new LinkedList<Rectangle>();

	/**
	 * first element of the queue defines the translation applied to each vertex
	 * coordinate used for 2D drawing
	 */
	private static LinkedList<Point> translation = new LinkedList<Point>();

	/**
	 * Create the singleton instance
	 */
	private static void init() {
		if (instance == null) {
			instance = new LegacyRasterizer();
			// TODO use VBO Rasterizer when possible
		} else {
			throw new IllegalStateException("Rasterizer was already created");
		}
	}

	/**
	 * @return the current rasterizer to use for 2D drawing operations
	 */
	public static Rasterizer getInstance() {
		if (instance == null)
			init();
		return instance;
	}

	/**
	 * Push a new translation matrix on top of the stack
	 * <p>
	 * Use this instead of direct glTranslate as it allows keeping track of
	 * nested matrix translations, so that nested Widget#render() calls can know
	 * precisely the render position on screen
	 * 
	 * @param x abscissa to add to the current translation
	 * @param y ordinate to add to the current translation
	 */
	public static void pushTranslate(int x, int y) {
		Point p = new Point(x, y);

		if (!translation.isEmpty()) {
			Point o = translation.getFirst();
			p.x += o.x;
			p.y += o.y;
		}

		translation.push(p);

		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0.0f);
	}

	/**
	 * Removes the last translation
	 * <p>
	 * Restores the matrix as it was before the last call to
	 * {@link #pushTranslate(int, int)}
	 */
	public static void popTranslate() {
		translation.pop();
		GL11.glPopMatrix();
	}

	/**
	 * Push new scissors on top of the Scissor stack
	 * <p>
	 * This handles calls to GL11.glScissor() so that stacking new scissors with
	 * relative positions is possible.
	 * <p>
	 * The position of the new scissor is relative to the current translation as
	 * accounted by {@link #pushTranslate(int, int)}
	 * 
	 * @param w new scissor width
	 * @param h new scissor height
	 */
	public static void pushScissor(int w, int h) {
		pushScissor(0, 0, w, h);
	}

	/**
	 * If unsure, you don't need this and should use
	 * {@link #pushScissor(int, int)} instead.
	 * <p>
	 * Otherwise, this allows specifying an additional translation for the
	 * scissor box instead of using the one from
	 * {@link #pushTranslate(int, int)} directly
	 * 
	 * @param x additional abscissa translation
	 * @param y additional ordinate translation
	 * @param w new scissor width
	 * @param h new scissor height
	 */
	public static void pushScissor(int x, int y, int w, int h) {
		Rectangle c = new Rectangle(x, y, w, h);

		if (!translation.isEmpty()) {
			Point p = translation.getFirst();
			c.x += p.x;
			c.y += p.y;
		}

		if (!scissors.isEmpty()) {
			Rectangle p = scissors.getFirst();
			c = p.intersection(c);
		}

		scissors.push(c);
		int dh = Display.getDisplayMode().getHeight();
		GL11.glScissor(c.x, dh - c.y - c.height, c.width, c.height);
	}

	/**
	 * Removes the last scissor
	 * <p>
	 * Restore the scissor box as it was before the last call to
	 * {@link #pushScissor(int, int)}
	 */
	public static void popScissor() {
		Rectangle c = scissors.pop();

		if (Config.get().isWmDebugLayout() && !c.isEmpty()) {

			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			int[] verts = new int[] {
					c.x, c.y, //
					c.x + c.width, c.y, //
					c.x + c.width, c.y + c.height, //
					c.x, c.y + c.height
			};
			float[] cols = Color.RED.toArray(8);
			instance.draw2DLineLoop(verts, cols);

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
		}

		if (scissors.size() > 0) {
			c = scissors.getFirst();
			int dh = Display.getDisplayMode().getHeight();
			GL11.glScissor(c.x, dh - c.y - c.height, c.width, c.height);
		} else {
			GL11.glScissor(0, 0, Display.getDisplayMode().getWidth(), Display
					.getDisplayMode().getHeight());
		}
	}

	public void drawQuad(int x, int y, int w, int h, Color col, float alpha) {
		Rectangle r = new Rectangle(x, y, w, h);

		if (!translation.isEmpty()) {
			Point trans = translation.getFirst();
			r.x += trans.x;
			r.y += trans.y;
		}
		if (!scissors.isEmpty()) {
			Rectangle sci = scissors.getFirst();
			r = sci.intersection(r);
		}
		if (r.isEmpty())
			return;

		float[] c = col.toArray(8, alpha);
		int[] v = {
				r.x, r.y, //
				r.x + r.width, r.y, //
				r.x + r.width, r.y + r.height, //
				r.x, r.y + r.height
		};
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		this.draw2DLineLoop(v, c);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	public void fillQuad(int x, int y, int w, int h, Color col, float alpha) {
		Rectangle r = new Rectangle(x, y, w, h);

		if (!translation.isEmpty()) {
			Point trans = translation.getFirst();
			r.x += trans.x;
			r.y += trans.y;
		}
		if (!scissors.isEmpty()) {
			Rectangle sci = scissors.getFirst();
			r = sci.intersection(r);
		}
		if (r.isEmpty())
			return;

		// TODO cache Color arrays
		float[] c = col.toArray(4, alpha);
		int[] v = {
				r.x, r.y, //
				r.x + r.width, r.y, //
				r.x + r.width, r.y + r.height, //
				r.x, r.y + r.height
		};
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		this.draw2D(v, c, GL11.GL_QUADS);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	public void fillQuad(int x, int y, int w, int h, Image img, float alpha) {
		// TODO recalculate tex coords when cropping with scissor
	}

	/**
	 * Draw a textured quad
	 * 
	 * @param vertices 4 2D vertices: 8 values
	 * @param colors 4 rgba components: 16 values
	 * @param texCoords 4 2D tex coords
	 * @param texture GL texture id
	 */
	public abstract void draw2DTexturedQuad(int[] vertices, float[] colors,
			int texture);

	/**
	 * Draw a textured quad
	 * 
	 * @param vertices 4 2D vertices: 8 values
	 * @param colors 4 rgba components: 16 values
	 * @param texCoords 4 2D tex coords
	 * @param texture GL texture id
	 */
	public abstract void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords, int texture);

	/**
	 * Draws a rectangle in line mode
	 * <p>
	 * When trying to draw pixel-accurate lines in 2D coordinates, always prefer
	 * this method as it:
	 * <ul>
	 * <li>ensures that corners will not be written twice (ie. when alpha < 1.0)
	 * <li>ensures that corners will not be skipped using offset magic
	 * </ul>
	 * Note that doing glTranslatef(0.375, 0.375, 0.) in MODELVIEW as frequently
	 * advertised does NOT guarantee pixel accuracy: you have to offset 0.5 top
	 * left and -0.3 bottom right.
	 * <p>
	 * The color array must contain the color for each pixel twice: the vertex
	 * array will indeed be reconstructed so that each line is drawn
	 * individually (sending 8 vertices and not 4)
	 * 
	 * @param vertices must be of size 8: 4 2D pixels. Order matters: should be
	 *            clockwise beginning at top left
	 * @param colors must be of size 32: 4 colors of 4rgba components, repeated
	 *            twice each
	 */
	protected abstract void draw2DLineLoop(int[] vertices, float[] colors);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	@Deprecated
	public abstract void draw2D(int[] vertices, float[] colors, int glMode);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	@Deprecated
	public abstract void draw2D(float[] vertices, float[] colors, int glMode);

	/**
	 * Draw arbitrary geometry in 2D space
	 * 
	 * @param vertices 2 coordinates per vertice: x,y
	 * @param colors 4 elements per color: r,g,b,a
	 * @param glMode one of the glBegin() primitives
	 */
	@Deprecated
	public abstract void draw2D(double[] vertices, float[] colors, int glMode);

}
