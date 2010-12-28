package bifstk.gl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.LinkedList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import bifstk.config.Config;

/**
 * Misc GL utilities
 * 
 */
public class Util {

	/**
	 * Uses Java2D to convert an AWT image to a LWJGL ByteBuffer
	 * 
	 * @param image an AWT image
	 * @return a ByteBuffer containing the image
	 */
	public static ByteBuffer imageToByteBuffer(BufferedImage image) {
		return imageToByteBuffer(image, false);
	}

	/**
	 * Uses Java2D to convert an AWT image to a LWJGL ByteBuffer
	 * 
	 * @param image an AWT image
	 * @param flip vertically mirrors the image if true
	 * @return a ByteBuffer containing the image
	 */
	public static ByteBuffer imageToByteBuffer(BufferedImage image, boolean flip) {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;
		int texWidth = Util.npot(image.getWidth());
		int texHeight = Util.npot(image.getHeight());
		int height = image.getHeight();

		ColorModel glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 8
				}, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		ColorModel glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
						8, 8, 8, 0
				}, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		boolean useAlpha = image.getColorModel().hasAlpha();

		if (useAlpha) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable<Object, Object>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable<Object, Object>());
		}

		// copy the source image into the produced image
		Graphics2D g = (Graphics2D) texImage.getGraphics();

		if (useAlpha) {
			g.setColor(new java.awt.Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, texWidth, texHeight);
		}

		if (flip) {
			g.scale(1.0, -1.0);
			g.drawImage(image, 0, -height, null);
		} else {
			g.drawImage(image, 0, 0, null);
		}

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();
		g.dispose();

		return imageBuffer;
	}

	/**
	 * @return {@link Rasterizer#getInstance()}
	 */
	public static Rasterizer raster() {
		return Rasterizer.getInstance();
	}

	/**
	 * Builds the geometry of an arc of a circle
	 * 
	 * @param cx abscissa center of the circle
	 * @param cy ordinate center of the circle
	 * @param r radius of the circle
	 * @param startAngle starting angle of the arc
	 * @param arcAngle angular length of the arc
	 * @param edges number of edge segments to draw the arc
	 * @param includeCenter include the center in the vertices return
	 * @return the vertices in 2D space: x,y,x,y,x,...,y
	 */
	public static double[] getArc(float cx, float cy, float r,
			float startAngle, float arcAngle, int edges, boolean includeCenter) {
		int len = edges;
		if (includeCenter) {
			len++;
		}
		double[] vertices = new double[len * 2];
		float theta = arcAngle / (float) (edges - 1);
		double tangetial_factor = Math.tan(theta);
		double radial_factor = Math.cos(theta);

		double x = r * Math.cos(startAngle);
		double y = r * Math.sin(startAngle);

		int b = 0;
		if (includeCenter) {
			vertices[0] = cx;
			vertices[1] = cy;
			b = 1;
		}

		for (int ii = b; ii < len; ii++) {
			vertices[ii * 2] = x + cx;
			vertices[ii * 2 + 1] = y + cy;

			double tx = -y;
			double ty = x;

			x += tx * tangetial_factor;
			y += ty * tangetial_factor;

			x *= radial_factor;
			y *= radial_factor;
		}
		return vertices;
	}

	/**
	 * Draws an arc circle as a colored line segment
	 * 
	 * @param cx abscissa center of the circle
	 * @param cy ordinate center of the circle
	 * @param r radius of the circle
	 * @param startAngle starting angle of the arc
	 * @param arcAngle angular length of the arc
	 * @param edges number of edge segments to draw the arc
	 * @param col line color
	 * @param alpha line opacity
	 */
	public static void drawLineArc(float cx, float cy, float r,
			float startAngle, float endAngle, int edges, Color col, float alpha) {
		double[] vert = getArc(cx, cy, r, startAngle, endAngle, edges, false);
		float[] cols = col.toArray(vert.length / 2, alpha);
		raster().draw2D(vert, cols, GL11.GL_LINE_STRIP);
	}

	/**
	 * Draws a filled arc circle
	 * 
	 * @param cx abscissa center of the circle
	 * @param cy ordinate center of the circle
	 * @param r radius of the circle
	 * @param startAngle starting angle of the arc
	 * @param arcAngle angular length of the arc
	 * @param edges number of edge segments to draw the arc
	 * @param inColor color of the center of the circle
	 * @param inAlpha opacity for inColor
	 * @param outColor color of the edge of the circle
	 * @param outAlpha opacity for outColor
	 */
	public static void drawFilledArc(float cx, float cy, float r,
			float startAngle, float arcAngle, int edges, Color inColor,
			float inAlpha, Color outColor, float outAlpha) {
		double[] verts = getArc(cx, cy, r, startAngle, arcAngle, edges, true);
		float[] cols = new float[(verts.length / 2) * 4];
		inColor.fillArray(cols, 0, 4, inAlpha);
		outColor.fillArray(cols, 4, cols.length, outAlpha);
		raster().draw2D(verts, cols, GL11.GL_TRIANGLE_FAN);
	}

	/**
	 * Draws a dropped shadow around the specified rectangle
	 * 
	 * 
	 * @param x abscissa of the bounds
	 * @param y ordinate of the bounds
	 * @param w width of the bounds
	 * @param h height of the bounds
	 * @param radius width of the shadow
	 * @param alpha opacity
	 * @param col color
	 */
	public static void drawDroppedShadow(int x, int y, int w, int h,
			int radius, float alpha, Color col) {
		int[] verts = {
				// shadow top
				x, y - radius, x + w, y - radius, x + w, y, x,
				y,
				// shadow right
				x + w + radius, y, x + w + radius, y + h, x + w, y + h, x + w,
				y,
				// shadow bot
				x + w, y + h + radius, x, y + h + radius, x, y + h, x + w,
				y + h,
				// shadow left
				x - radius, y + h, x - radius, y, x, y, x, y + h,
				// center
				x, y, x, y + h, x + w, y + h, x + w, y
		};
		float[] cols = new float[2 * verts.length];
		col.fillArray(cols, 0, 4 * 2, 0.0f);
		col.fillArray(cols, 4 * 2, 4 * 4, alpha);
		col.fillArray(cols, 4 * 4, 4 * 6, 0.0f);
		col.fillArray(cols, 4 * 6, 4 * 8, alpha);
		col.fillArray(cols, 4 * 8, 4 * 10, 0.0f);
		col.fillArray(cols, 4 * 10, 4 * 12, alpha);
		col.fillArray(cols, 4 * 12, 4 * 14, 0.0f);
		col.fillArray(cols, 4 * 14, 4 * 16, alpha);
		col.fillArray(cols, 4 * 16, 4 * 20, alpha);

		raster().draw2D(verts, cols, GL11.GL_QUADS);

		int precision = 5;

		Util.drawFilledArc((float) x, (float) y, (float) radius,
				(float) Math.PI, (float) Math.PI / 2.0f, precision, col, alpha,
				col, 0.0f);
		Util.drawFilledArc((float) x + w, (float) y, (float) radius,
				(float) -Math.PI / 2.0f, (float) Math.PI / 2.0f, precision,
				col, alpha, col, 0.0f);
		Util.drawFilledArc((float) x + w, (float) y + h, (float) radius, 0.0f,
				(float) Math.PI / 2.0f, precision, col, alpha, col, 0.0f);
		Util.drawFilledArc((float) x, (float) y + h, (float) radius,
				(float) Math.PI / 2.0f, (float) Math.PI / 2.0f, precision, col,
				alpha, col, 0.0f);
	}

	private static LinkedList<Rectangle> scissors = new LinkedList<Rectangle>();

	private static LinkedList<Point> translation = new LinkedList<Point>();

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
		Util.pushScissor(0, 0, w, h);
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
			raster().draw2DLineLoop(verts, cols);

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

	/**
	 * Clamp integer in specified range
	 * 
	 * @param val value to clamp
	 * @param min min value
	 * @param max max value
	 * @return a value comprised between min and max
	 */
	public static int clampi(int val, int min, int max) {
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
	public static float clampf(float val, float min, float max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Next Power Of Two
	 * 
	 * @param n a positive integer
	 * @return the lesser power of two greater than n
	 */
	public static int npot(int n) {
		int k = 1;
		while (k < n) {
			k *= 2;
		}
		return k;
	}

}
