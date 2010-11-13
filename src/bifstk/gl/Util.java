package bifstk.gl;

import java.awt.Graphics2D;
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

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture

		while (texWidth < image.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < image.getHeight()) {
			texHeight *= 2;
		}

		int height = image.getHeight();

		ColorModel glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		ColorModel glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, ComponentColorModel.OPAQUE,
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
	 * Draws an arc of a circle
	 * <p>
	 * This method does not perform any glBegin(), it only defines vertices with
	 * glVertex2d()
	 * 
	 * @param cx abscissa center of the circle
	 * @param cy ordinate center of the circle
	 * @param r radius of the circle
	 * @param startAngle starting angle of the arc
	 * @param arcAngle angular length of the arc
	 * @param edges number of edge segments to draw the arc
	 */
	public static void drawArc(float cx, float cy, float r, float startAngle,
			float arcAngle, int edges) {

		float theta = arcAngle / (float) (edges - 1);
		double tangetial_factor = Math.tan(theta);
		double radial_factor = Math.cos(theta);

		double x = r * Math.cos(startAngle);
		double y = r * Math.sin(startAngle);

		for (int ii = 0; ii < edges; ii++) {
			GL11.glVertex2d(x + cx, y + cy);

			double tx = -y;
			double ty = x;

			x += tx * tangetial_factor;
			y += ty * tangetial_factor;

			x *= radial_factor;
			y *= radial_factor;
		}
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
		GL11.glBegin(GL11.GL_LINE_STRIP);
		col.use(alpha);
		drawArc(cx, cy, r, startAngle, endAngle, edges);
		GL11.glEnd();
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
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		inColor.use(inAlpha);
		GL11.glVertex2f(cx, cy);
		outColor.use(outAlpha);
		drawArc(cx, cy, r, startAngle, arcAngle, edges);
		GL11.glEnd();
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
		GL11.glBegin(GL11.GL_QUADS);
		// shadow: top
		col.use(0.0f);
		GL11.glVertex2i(x, y - radius);
		GL11.glVertex2i(x + w, y - radius);
		col.use(alpha);
		GL11.glVertex2i(x + w, y);
		GL11.glVertex2i(x, y);
		// shadow: right
		col.use(0.0f);
		GL11.glVertex2i(x + w + radius, y);
		GL11.glVertex2i(x + w + radius, y + h);
		col.use(alpha);
		GL11.glVertex2i(x + w, y + h);
		GL11.glVertex2i(x + w, y);
		// shadow: bot
		col.use(0.0f);
		GL11.glVertex2i(x + w, y + h + radius);
		GL11.glVertex2i(x, y + h + radius);
		col.use(alpha);
		GL11.glVertex2i(x, y + h);
		GL11.glVertex2i(x + w, y + h);
		// shadow: left
		col.use(0.0f);
		GL11.glVertex2i(x - radius, y + h);
		GL11.glVertex2i(x - radius, y);
		col.use(alpha);
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x, y + h);
		// center
		col.use(alpha);
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x, y + h);
		GL11.glVertex2i(x + w, y + h);
		GL11.glVertex2i(x + w, y);
		GL11.glEnd();

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

	/**
	 * Abscissa, ordinate, width and height in pixels
	 * <p>
	 * Used for scissor boxes
	 */
	private static class Coord {
		public int x, cx = 0;
		public int y, cy = 0;
		public int w;
		public int h;

		public Coord(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	/** stack of scissor boxes */
	private static LinkedList<Coord> scissors = new LinkedList<Coord>();

	/**
	 * Push a new Scissor box in the OpenGL context
	 * <p>
	 * This call can be reverted with a subsequent call to {@link #popScissor()}
	 * <p>
	 * This is useful since glScissor does not into take account the current
	 * MODELVIEW transformations and uses absolute screen coordinates which can
	 * be unknown in some contexts
	 * <p>
	 * The new scissor box can be clamped so that it is contained in the
	 * previous one
	 * 
	 * @param ax value to add to the current scissor abscissa value
	 * @param ay value to add to the current scissor ordinate value, in absolute
	 *            screen coordinates, ie with the origin at the bottom
	 * @param w new scissor box width
	 * @param h new scissor box height
	 */
	public static void pushScissor(int ax, int ay, int w, int h) {
		int bx = 0;
		int by = 0;
		int bw = Display.getDisplayMode().getWidth();
		int bh = Display.getDisplayMode().getHeight();

		if (scissors.size() > 0) {
			Coord last = scissors.getFirst();
			bx = last.x;
			by = last.y;
			bw = last.w;
			bh = last.h;

			if (ay < (-last.cy)) {
				h += (ay + last.cy);
			}
			ay += last.cy;

			if (ax < (-last.cx)) {
				h += (ax + last.cx);
			}
			ax += last.cx;

		}

		int nx = clampi(ax, 0, bw);
		int ny = clampi(ay, 0, bh);
		int nw = clampi(w, 0, bw - nx);
		int nh = clampi(h, 0, bh - ny);

		Coord sci = new Coord(bx + nx, by + ny, nw, nh);
		scissors.push(sci);

		sci.cx = ax - nx;
		sci.cy = ay - ny;

		if (new Boolean(Config.isWmDebugLayout())) {
			int dh = Display.getDisplayMode().getHeight();
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glBegin(GL11.GL_LINE_LOOP);
			bifstk.gl.Color.RED.use();
			GL11.glVertex2i(sci.x, dh - sci.y - sci.h);
			GL11.glVertex2i(sci.x + sci.w, dh - sci.y - sci.h);
			GL11.glVertex2i(sci.x + sci.w, dh - sci.y);
			GL11.glVertex2i(sci.x, dh - sci.y);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
		}

		GL11.glScissor(sci.x, sci.y, sci.w, sci.h);

	}

	/**
	 * Reset the scissor box as it was last time
	 * {@link #pushScissor(int, int, int, int)} was called
	 */
	public static void popScissor() {
		scissors.pop();
		if (scissors.size() > 0) {
			Coord sci = scissors.getFirst();
			GL11.glScissor(sci.x, sci.y, sci.w, sci.h);
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
