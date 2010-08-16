package bifstk.gl;

import java.nio.IntBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Misc GL utilities
 * 
 */
public class Util {

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
	 * Draws a filled arc circle
	 * 
	 * @param cx abscissa center of the circle
	 * @param cy ordinate center of the circle
	 * @param r radius of the circle
	 * @param startAngle starting angle of the arc
	 * @param arcAngle angular length of the arc
	 * @param edges number of edge segments to draw the arc
	 * @param inColor color of the center of the circle
	 * @param outColor color of the edge of the circle
	 */
	public static void drawFilledArc(float cx, float cy, float r,
			float startAngle, float arcAngle, int edges, Color inColor,
			Color outColor) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		inColor.use();
		GL11.glVertex2f(cx, cy);
		outColor.use();
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
	 */
	public static void drawDroppedShadow(int x, int y, int w, int h,
			int radius, float alpha) {
		Color shadowCol = new Color(0.0f, 0.0f, 0.0f, alpha);

		GL11.glBegin(GL11.GL_QUADS);
		// shadow: top
		Color.TRANSP_BLACK.use();
		GL11.glVertex2i(x, y - radius);
		GL11.glVertex2i(x + w, y - radius);
		shadowCol.use();
		GL11.glVertex2i(x + w, y);
		GL11.glVertex2i(x, y);
		// shadow: right
		Color.TRANSP_BLACK.use();
		GL11.glVertex2i(x + w + radius, y);
		GL11.glVertex2i(x + w + radius, y + h);
		shadowCol.use();
		GL11.glVertex2i(x + w, y + h);
		GL11.glVertex2i(x + w, y);
		// shadow: bot
		Color.TRANSP_BLACK.use();
		GL11.glVertex2i(x + w, y + h + radius);
		GL11.glVertex2i(x, y + h + radius);
		shadowCol.use();
		GL11.glVertex2i(x, y + h);
		GL11.glVertex2i(x + w, y + h);
		// shadow: left
		Color.TRANSP_BLACK.use();
		GL11.glVertex2i(x - radius, y + h);
		GL11.glVertex2i(x - radius, y);
		shadowCol.use();
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x, y + h);

		GL11.glEnd();

		int precision = 5;

		Util.drawFilledArc((float) x, (float) y, (float) radius,
				(float) Math.PI, (float) Math.PI / 2.0f, precision, shadowCol,
				Color.TRANSP_BLACK);
		Util.drawFilledArc((float) x + w, (float) y, (float) radius,
				(float) -Math.PI / 2.0f, (float) Math.PI / 2.0f, precision,
				shadowCol, Color.TRANSP_BLACK);
		Util.drawFilledArc((float) x + w, (float) y + h, (float) radius, 0.0f,
				(float) Math.PI / 2.0f, precision, shadowCol,
				Color.TRANSP_BLACK);
		Util.drawFilledArc((float) x, (float) y + h, (float) radius,
				(float) Math.PI / 2.0f, (float) Math.PI / 2.0f, precision,
				shadowCol, Color.TRANSP_BLACK);
	}

	/**
	 * Abscissa, ordinate, width and height in pixels
	 * <p>
	 * Used for scissor boxes
	 */
	private static class Coord {
		public int x;
		public int y;
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
	 * 
	 * 
	 * @param ax value to add to the current scissor abscissa value
	 * @param ay value to add to the current scissor ordinate value, in absolute
	 *            screen coordinates, ie with the origin at the bottom
	 * @param w new scissor box width
	 * @param h new scissor box height
	 */
	public static void pushScissor(int ax, int ay, int w, int h) {
		IntBuffer buf = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_SCISSOR_BOX, buf);
		int bx = buf.get(), by = buf.get();
		int bw = buf.get(), bh = buf.get();

		Coord sci = new Coord(bx, by, bw, bh);
		scissors.push(sci);

		GL11.glScissor(bx + ax, by + ay, w, h);

	}

	/**
	 * Reset the scissor box as it was last time
	 * {@link #pushScissor(int, int, int, int)} was called
	 */
	public static void popScissor() {
		Coord sci = scissors.pop();
		GL11.glScissor(sci.x, sci.y, sci.w, sci.h);
	}

}
