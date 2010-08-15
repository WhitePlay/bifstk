package bifstk.wm.ui;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import bifstk.gl.Color;
import bifstk.wm.geom.Rectangle;

/**
 * Container that stacks widgets horizontally or vertically
 * <p>
 * Each widget will fill a given percentage of the total available space
 * depending the specified weight upon insertion
 * 
 */
public class Box implements Widget {

	/**
	 * Orientation of the widgets contained by the box: left to right if
	 * {@link #HORIZONTAL} , top to bottom if {@link #VERTICAL}
	 */
	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	/**
	 * Associates a widget with its weight
	 */
	private static class Entry {
		Widget widget = null;
		float weight = 1.0f;

		public Entry(Widget widget) {
			this(widget, 1.0f);
		}

		public Entry(Widget widget, float weight) {
			this.widget = widget;
			this.weight = Math.max(0.0f, weight);
		}
	}

	/** widgets contained by this box along with their weight */
	private List<Entry> children = null;

	/** dimension of the box */
	private Rectangle bounds = null;

	/** width of the border between each element */
	private int borderWidth = 2;

	// TODO REMOVE this should be handled by the theme, only used for debug
	private Color backgroundColor = Color.WHITE;

	/** orientation of the box: vertical or horizontal */
	private Orientation orientation;

	/**
	 * Default constructor
	 * 
	 * @param orientation orientation of the box
	 */
	public Box(Orientation orientation) {
		this(orientation, 2);
	}

	/**
	 * Default constructor
	 * 
	 * @param orientation orientation of the box
	 * @param borderWidth pixel border between each element
	 */
	public Box(Orientation orientation, int borderWidth) {
		this.children = new ArrayList<Entry>();
		this.borderWidth = borderWidth;
		this.orientation = orientation;
		this.bounds = new Rectangle();
	}

	/**
	 * Called when the geometry of the box has changed: need to subsequently
	 * resize all children widgets
	 */
	private void resize() {
		if (!this.hasChildren()) {
			return;
		}
		int a, b;

		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			a = this.getWidth();
			b = this.getHeight();
		} else {
			a = this.getHeight();
			b = this.getWidth();
		}

		int border = this.getBorderWidth();

		float totalWeight = 0;
		for (Entry entry : children) {
			totalWeight += entry.weight;
		}

		int pixels = a - border * (children.size() + 1);
		int usedPixels = 0;
		float usedPixels2 = 0.0f;
		for (Entry ent : children) {
			float weight = ent.weight / totalWeight;
			int px = (int) (weight * pixels);
			usedPixels += px;
			usedPixels2 += weight * pixels;
			if (usedPixels2 - (float) usedPixels >= 1.0f) {
				usedPixels++;
				usedPixels2 += 1.0f;
				px++;
			}
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				ent.widget.setWidth(px);
				ent.widget.setHeight(b - border * 2);
			} else {
				ent.widget.setHeight(px);
				ent.widget.setWidth(b - border * 2);
			}
		}
	}

	@Override
	public void render(float alpha) {

		int w = this.getWidth();
		int h = this.getHeight();

		if (this.hasChildren()) {
			int border = this.getBorderWidth();

			this.getBackgroundColor().use(alpha);
			GL11.glBegin(GL11.GL_QUADS);
			// top border
			GL11.glVertex2i(border, 0);
			GL11.glVertex2i(w, 0);
			GL11.glVertex2i(w, border);
			GL11.glVertex2i(border, border);

			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				// bot border
				GL11.glVertex2i(w, h);
				GL11.glVertex2i(border, h);
				GL11.glVertex2i(border, h - border);
				GL11.glVertex2i(w, h - border);
			} else {
				// right border
				GL11.glVertex2i(w, border);
				GL11.glVertex2i(w, h);
				GL11.glVertex2i(w - border, h);
				GL11.glVertex2i(w - border, border);
			}

			// left border
			GL11.glVertex2i(0, 0);
			GL11.glVertex2i(0, h);
			GL11.glVertex2i(border, h);
			GL11.glVertex2i(border, 0);
			GL11.glEnd();

			int acc = border;
			for (Entry child : this.children) {
				Widget c = child.widget;

				// get the current scissor box ; clumsy but no other way since
				// at this point we have been glTranslate and the scissor has to
				// be specified in absolute screen coordinates
				IntBuffer buf = BufferUtils.createIntBuffer(16);
				GL11.glGetInteger(GL11.GL_SCISSOR_BOX, buf);
				int bx = buf.get(), by = buf.get();
				int bw = buf.get(), bh = buf.get();

				GL11.glPushMatrix();
				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					GL11.glTranslatef(acc, border, 0);
					// scissors use bot-left as origin as opposed to us..
					GL11.glScissor(bx + acc, by + border, c.getWidth(),
							c.getHeight());
				} else {
					GL11.glTranslatef(border, acc, 0);
					GL11.glScissor(bx + border, by + h - acc - c.getHeight(),
							c.getWidth(), c.getHeight());
				}

				c.render(alpha);

				GL11.glScissor(bx, by, bw, bh);
				GL11.glPopMatrix();

				this.getBackgroundColor().use(alpha);
				GL11.glBegin(GL11.GL_QUADS);
				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					// right border
					acc += c.getWidth();
					GL11.glVertex2i(acc, border);
					GL11.glVertex2i(acc, h - border);
					GL11.glVertex2i(acc + border, h - border);
					GL11.glVertex2i(acc + border, border);
				} else {
					// bot border
					acc += c.getHeight();
					GL11.glVertex2i(border, acc);
					GL11.glVertex2i(w - border, acc);
					GL11.glVertex2i(w - border, acc + border);
					GL11.glVertex2i(border, acc + border);

				}
				GL11.glEnd();

				acc += border;

			}
		} else {
			this.getBackgroundColor().use(alpha);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(0, 0);
			GL11.glVertex2i(w, 0);
			GL11.glVertex2i(w, h);
			GL11.glVertex2i(0, h);
			GL11.glEnd();

		}

		GL11.glEnd();
	}

	/**
	 * @param b width in pixels of the border between contained widgets
	 */
	public void setBorderWidth(int b) {
		this.borderWidth = b;
	}

	/**
	 * @return the width in pixels of the border between contained widgets
	 */
	public int getBorderWidth() {
		return this.borderWidth;
	}

	/**
	 * Append a widget to the container; will be added at the end of the box
	 * 
	 * @param w new widget to append to the box
	 */
	public void addChild(Widget w) {
		this.children.add(new Entry(w));
	}

	/**
	 * Append a widget to the container; will be added at the end of the box
	 * 
	 * @param w new widget to append to the box
	 * @param weight weight of the new widget
	 */
	public void addChild(Widget w, float weight) {
		this.children.add(new Entry(w, weight));
	}

	/**
	 * Remove a given widget from the container
	 * 
	 * @param w widget to remove
	 */
	public void removeChild(Widget w) {
		Entry torem = null;
		for (Entry e : this.children) {
			if (e.widget.equals(w)) {
				torem = e;
			}
		}
		if (torem != null) {
			this.children.remove(torem);
		}
	}

	/**
	 * @return true if this Box contains widgets, or false
	 */
	public boolean hasChildren() {
		return this.children != null && this.children.size() > 0;
	}

	/**
	 * Remove all widgets contained in this box
	 */
	public void clearChildren() {
		this.children.clear();
	}

	@Override
	public void setWidth(int w) {
		this.bounds.setWidth(w);
		resize();
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public void setHeight(int h) {
		this.bounds.setHeight(h);
		resize();
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	@Override
	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
		resize();
	}

	// TODO REMOVE this should be handled by the theme, only used for debug
	public void setBackgroundColor(Color c) {
		this.backgroundColor = c;
	}

	// TODO REMOVE this should be handled by the theme, only used for debug
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}
}
