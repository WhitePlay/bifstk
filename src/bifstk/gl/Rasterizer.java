package bifstk.gl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import bifstk.config.Config;
import bifstk.config.TextureLoader;

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
	 * Clockwise rotation in degrees
	 */
	public static enum Rotation {
		ROTATE_0, ROTATE_90, ROTATE_180, ROTATE_270;
	}

	protected static float[] coord_0 = {
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
	};
	protected static float[] coord_90 = {
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f
	};
	protected static float[] coord_180 = {
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
	};
	protected static float[] coord_270 = {
			1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f
	};

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
	}

	/**
	 * Removes the last translation
	 * <p>
	 * Restores the matrix as it was before the last call to
	 * {@link #pushTranslate(int, int)}
	 */
	public static void popTranslate() {
		translation.pop();
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
			instance.drawQuad(0, 0, c.width, c.height, Color.RED, 1.0f);
		}
	}

	/**
	 * Draw the outline of a 2D Quad
	 * <p>
	 * 
	 * @param x top left abscissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w quad width
	 * @param h quad height
	 * @param col color
	 * @param alpha alpha factor for the color
	 */
	public void drawQuad(int x, int y, int w, int h, Color col, float alpha) {
		/* drawing the contour of a quad is achieved by
		 * drawing 4 1-pixel thick quads
		 * 
		 * this allows working around GL_LINES rasterization accuracy in OpenGL,
		 * which differs depending the driver
		 * 
		 * Also, this allows using only GL_QUADS for all drawing operations:
		 * all the geometry can be sent to the GPU in one batch per render,
		 * providing important performance improvement
		 */

		// top left : top right
		fillQuad(x + 1, y, //
				w - 1, 1, //
				col, alpha);
		// top right : bot right
		fillQuad(x + w - 1, y + 1, //
				1, h - 1, //
				col, alpha);
		// bot right : bot left
		fillQuad(x, y + h - 1, //
				w - 1, 1, //
				col, alpha);
		// bot left : top left
		fillQuad(x, y, //
				1, h - 1, //
				col, alpha);

	}

	/**
	 * Fill a 2D Quad with a solid color
	 * 
	 * @param x top left abscissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w quad width
	 * @param h quad height
	 * @param col color
	 * @param alpha alpha factor for the color
	 */
	public void fillQuad(int x, int y, int w, int h, Color col, float alpha) {
		this.fillQuad(x, y, w, h, TextureLoader.getBlank(), col, alpha,
				Rotation.ROTATE_0);
	}

	/**
	 * Fill a 2D Quad with a gradient color
	 * 
	 * @param x top left abscissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w quad width
	 * @param h quad height
	 * @param top color of the two top vertices
	 * @param bot color of the two bottom vertices
	 * @param alphaTop alpha factor of the two top vertices
	 * @param alphaBot alpha factor of the two bottom vertices
	 */
	public void fillQuad(int x, int y, int w, int h, Color top, Color bot,
			float alphaTop, float alphaBot) {
		float[] c = new float[4 * 4];
		top.fillArray(c, 0, 8, alphaTop);
		bot.fillArray(c, 8, 16, alphaBot);
		this.fillQuad(x, y, w, h, TextureLoader.getBlank(), c,
				Rotation.ROTATE_0);
	}

	/**
	 * Fill a 2D Quad with a texture
	 * 
	 * @param x top left ascissa coordinate
	 * @param y top left ordinate coordinate
	 * @param img texture image
	 * @param alpha alpha factor for the image
	 */
	public void fillQuad(int x, int y, Image img, float alpha) {
		fillQuad(x, y, img, Color.WHITE, alpha);
	}

	/**
	 * Fill a 2D Quad with a texture
	 * 
	 * @param x top left ascissa coordinate
	 * @param y top left ordinate coordinate
	 * @param img texture image
	 * @param col texture color
	 * @param alpha alpha factor for the image
	 */
	public void fillQuad(int x, int y, Image img, Color col, float alpha) {
		fillQuad(x, y, img.getWidth(), img.getHeight(), img, col, alpha,
				Rotation.ROTATE_0);
	}

	/**
	 * Fill a 2D Quad with a texture
	 * 
	 * @param x top left ascissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w quad width
	 * @param h quad height
	 * @param img texture image
	 * @param col texture color
	 * @param alpha alpha factor for the image
	 * @param rotation use one of
	 */
	public void fillQuad(int x, int y, int w, int h, Image img, Color col,
			float alpha, Rotation rotation) {
		float[] c = col.toArray(4, alpha);
		fillQuad(x, y, w, h, img, c, rotation);
	}

	/**
	 * Fill a 2D Quad with a texture
	 * 
	 * @param x top left ascissa coordinate
	 * @param y top left ordinate coordinate
	 * @param w quad width
	 * @param h quad height
	 * @param img texture image
	 * @param col texture color: 4 * rgba
	 * @param rotation use one of
	 */
	private void fillQuad(int x, int y, int w, int h, Image img, float[] col,
			Rotation rotation) {
		Rectangle r = new Rectangle(x, y, w, h);

		if (!translation.isEmpty()) {
			Point trans = translation.getFirst();
			r.x += trans.x;
			r.y += trans.y;
		}

		if (r.isEmpty())
			return;

		Rectangle imgBounds = new Rectangle(img.getTexX(), img.getTexY(),
				img.getWidth(), img.getHeight());

		if (!scissors.isEmpty()) {
			Rectangle sci = scissors.getFirst();

			Rectangle tmp = new Rectangle(r.x, r.y, imgBounds.width,
					imgBounds.height);
			Rectangle crop = sci.intersection(tmp);

			// this test kind of sucks but this is a truly unique special case
			if (!img.equals(TextureLoader.getBlank())) {
				imgBounds.x += (crop.x - r.x);
				imgBounds.y += (crop.y - r.y);
				imgBounds.width = crop.width;
				imgBounds.height = crop.height;
			}

			r = r.intersection(sci);
			if (r.isEmpty())
				return;

		}

		int[] v = {
				r.x, r.y, //
				r.x + r.width, r.y, //
				r.x + r.width, r.y + r.height, //
				r.x, r.y + r.height
		};

		float sx = (float) imgBounds.x / (float) img.getTexWidth();
		float sy = (float) imgBounds.y / (float) img.getTexHeight();
		float rx = (float) imgBounds.width / (float) img.getTexWidth();
		float ry = (float) imgBounds.height / (float) img.getTexHeight();

		float[] cf = null;
		switch (rotation) {
		case ROTATE_0:
			cf = coord_0;
			break;
		case ROTATE_90:
			cf = coord_90;
			break;
		case ROTATE_180:
			cf = coord_180;
			break;
		case ROTATE_270:
			cf = coord_270;
			break;
		}

		float[] coords = {
				sx + rx * cf[0], sy + ry * cf[1], //
				sx + rx * cf[2], sy + ry * cf[3], //
				sx + rx * cf[4], sy + ry * cf[5], //
				sx + rx * cf[6], sy + ry * cf[7]
		};

		this.draw2DTexturedQuad(v, col, coords);

	}

	/**
	 * Draw a textured quad
	 * 
	 * @param vertices 4 2D vertices: 8 values
	 * @param colors 4 rgba components: 16 values
	 * @param texCoords 4 2D tex coords
	 */
	protected abstract void draw2DTexturedQuad(int[] vertices, float[] colors,
			float[] texCoords);

	/**
	 * This Rasterizer might be buffering current drawing operations so that all
	 * geometry is sent in one batch.
	 * <p>
	 * Calling this method causes the actual drawing off all buffered geometry
	 */
	public abstract void flush();
}
