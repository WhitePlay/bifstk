package bifstk.wm.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import bifstk.config.Theme;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Container that stacks Widgets horizontally
 * <p>
 * Depending which method is used to add a Widget in the FlowBox, Widgets are
 * laid out as follows:
 * 
 * <pre>
 * FlowBox b = new FlowBox();
 * b.addLeft(w1);
 * b.addRight(w2);
 * b.setExpand(w3);
 * b.addLeft(w4);
 *  ______________________________
 * | w1 | w4 |       w3      | w2 |
 * |____|____|_______________|____|
 * </pre>
 * 
 * w1 and w4 are added in the left flow, w2 is added in the right flow. Widgets
 * added in the left or right flow are displayed using their preferred size (see
 * {@link Widget#getPreferredWidth()}.
 * <p>
 * w3 is displayed between the left and right flow, and expanded using all the
 * height and width available.
 * <p>
 * There can be only one expanded widget.
 * 
 * 
 */
public class FlowBox extends Container {

	/** widgets in fixed mode contained by this box, left from the expansion */
	private List<Widget> leftChildren = null;

	/** widgets in fixed mode contained by this box, right from the expansion */
	private List<Widget> rightChildren = null;

	/** widget in expand mode contained by this box */
	private Widget expandChild = null;

	/** dimension of the box */
	private Rectangle bounds = null;

	/** width of the border between each element */
	private int borderWidth = 2;

	/**
	 * Default constructor
	 */
	public FlowBox() {
		this(2);
	}

	/**
	 * Default constructor
	 * 
	 * @param border pixel border between each element
	 */
	public FlowBox(int border) {
		this.leftChildren = new ArrayList<Widget>();
		this.rightChildren = new ArrayList<Widget>();
		this.expandChild = null;
		this.borderWidth = border;
		this.bounds = new Rectangle();
	}

	// true if expandChild has room to be rendered
	private boolean drawExpanded = false;

	/**
	 * Called when the geometry of the box has changed: need to subsequently
	 * resize children widgets
	 */
	private void resize() {
		int tw = 0;
		for (Widget w : this.leftChildren) {
			tw += w.getPreferredWidth();
			w.setBounds(w.getPreferredWidth(), w.getPreferredHeight());
		}
		for (Widget w : this.rightChildren) {
			tw += w.getPreferredWidth();
			w.setBounds(w.getPreferredWidth(), w.getPreferredHeight());
		}
		int ew = this.getWidth() - tw;
		if (this.expandChild != null && ew > 0) {
			int exPw = expandChild.getPreferredHeight();
			if (exPw <= 0) {
				exPw = this.getHeight();
			}
			this.expandChild.setBounds(ew, exPw);
			this.drawExpanded = true;
		} else {
			this.drawExpanded = false;
		}
	}

	@Override
	public void render(float alpha) {
		int w = this.getWidth();
		int h = this.getHeight();

		if (!hasChildren()) {
			Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(0, 0);
			GL11.glVertex2i(w, 0);
			GL11.glVertex2i(w, h);
			GL11.glVertex2i(0, h);
			GL11.glEnd();
		} else {
			int acc = 0;

			for (Widget left : this.leftChildren) {
				if (acc > w) {
					break;
				}
				GL11.glPushMatrix();
				GL11.glTranslatef(acc, 0, 0);
				Util.pushScissor(acc, h - left.getHeight(),
						Math.min(left.getWidth(), w - acc), left.getHeight());
				left.render(alpha);
				Util.popScissor();
				GL11.glPopMatrix();

				if (left.getHeight() < h) {
					Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2i(acc, left.getHeight());
					GL11.glVertex2i(acc + left.getWidth(), left.getHeight());
					GL11.glVertex2i(acc + left.getWidth(), h);
					GL11.glVertex2i(acc, h);
					GL11.glEnd();
				}

				acc += left.getWidth();
			}

			if (drawExpanded) {
				GL11.glPushMatrix();
				GL11.glTranslatef(acc, 0, 0);
				Util.pushScissor(acc, h - expandChild.getHeight(),
						this.expandChild.getWidth(),
						this.expandChild.getHeight());
				this.expandChild.render(alpha);
				Util.popScissor();
				GL11.glPopMatrix();

				if (expandChild.getHeight() < h) {
					Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2i(acc, expandChild.getHeight());
					GL11.glVertex2i(acc + expandChild.getWidth(),
							expandChild.getHeight());
					GL11.glVertex2i(acc + expandChild.getWidth(), h);
					GL11.glVertex2i(acc, h);
					GL11.glEnd();
				}

				acc += this.expandChild.getWidth();
			}

			for (Widget right : this.rightChildren) {
				if (acc > w) {
					break;
				}
				GL11.glPushMatrix();
				GL11.glTranslatef(acc, 0, 0);
				Util.pushScissor(acc, h - right.getHeight(),
						Math.min(right.getWidth(), w - acc), right.getHeight());
				right.render(alpha);
				Util.popScissor();
				GL11.glPopMatrix();

				if (right.getHeight() < h) {
					Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2i(acc, right.getHeight());
					GL11.glVertex2i(acc + right.getWidth(), right.getHeight());
					GL11.glVertex2i(acc + right.getWidth(), h);
					GL11.glVertex2i(acc, h);
					GL11.glEnd();
				}

				acc += right.getWidth();
			}
		}
	}

	/**
	 * Append a widget to the container; will be added at the end of the left
	 * flow in fixed fill mode
	 * 
	 * @param w new widget to append to the left flow
	 */
	public void addLeft(Widget w) {
		super.add(w);
		this.leftChildren.add(w);
		resize();
	}

	/**
	 * Append a widget to the container; will be added at the end of the right
	 * flow in fixed fill mode
	 * 
	 * @param w new widget to append to the right flow
	 */
	public void addRight(Widget w) {
		super.add(w);
		this.rightChildren.add(w);
		resize();
	}

	/**
	 * @param w the Widget to put in expand mode between the left and right flow
	 */
	public void setExpand(Widget w) {
		super.add(w);
		if (this.expandChild != null) {
			this.expandChild.setParent(null);
		}
		this.expandChild = w;
		resize();
	}

	@Override
	public void removeChild(Widget w) {
		if (this.leftChildren.remove(w) || this.rightChildren.remove(w)) {
			w.setParent(null);
			resize();
		} else if (this.expandChild.equals(w)) {
			w.setParent(null);
			this.expandChild = null;
			resize();
		}
	}

	@Override
	public boolean hasChildren() {
		return this.leftChildren.size() > 0 || this.expandChild != null
				|| this.rightChildren.size() > 0;
	}

	@Override
	public void clearChildren() {
		for (Widget w : this.leftChildren) {
			w.setParent(null);
		}
		this.leftChildren.clear();
		this.expandChild.setParent(null);
		this.expandChild = null;
		for (Widget w : this.rightChildren) {
			w.setParent(null);
		}
		this.rightChildren.clear();
		resize();
	}

	@Override
	public void setWidth(int w) {
		if (this.bounds.getWidth() != w) {
			this.bounds.setWidth(w);
			resize();
		}
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);
			resize();
		}
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);
			resize();
		}
	}

	@Override
	public int getPreferredWidth() {
		int cw = 0;
		for (Widget w : this.leftChildren) {
			cw += w.getPreferredWidth();
		}
		if (this.expandChild != null) {
			cw += this.expandChild.getPreferredWidth();
		}
		for (Widget w : this.rightChildren) {
			cw += w.getPreferredWidth();
		}
		return cw;
	}

	@Override
	public int getPreferredHeight() {
		int mh = 0;
		for (Widget w : this.leftChildren) {
			mh = Math.max(mh, w.getPreferredHeight());
		}
		if (this.expandChild != null) {
			mh = Math.max(mh, this.expandChild.getPreferredHeight());
		}
		for (Widget w : this.rightChildren) {
			mh = Math.max(mh, w.getPreferredHeight());
		}
		return mh;
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

}
