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

	/**
	 * Orientation of the widgets contained by the box: left to right if
	 * {@link #HORIZONTAL} , top to bottom if {@link #VERTICAL}
	 */
	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	/** orientation of the box: vertical or horizontal */
	private Orientation orientation;

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
	public FlowBox(Orientation orient) {
		this(orient, 2);
	}

	/**
	 * Default constructor
	 * 
	 * @param border pixel border between each element
	 */
	public FlowBox(Orientation orient, int border) {
		this.leftChildren = new ArrayList<Widget>();
		this.rightChildren = new ArrayList<Widget>();
		this.expandChild = null;
		this.borderWidth = border;
		this.bounds = new Rectangle();
		this.orientation = orient;
	}

	// true if expandChild has room to be rendered
	private boolean drawExpanded = false;

	/**
	 * Called when the geometry of the box has changed: need to subsequently
	 * resize children widgets
	 */
	private void resize() {
		int tacc = 0;

		for (Widget w : this.leftChildren) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				tacc += w.getPreferredWidth();
			} else {
				tacc += w.getPreferredHeight();
			}
			w.setBounds(w.getPreferredWidth(), w.getPreferredHeight());
		}

		for (Widget w : this.rightChildren) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				tacc += w.getPreferredWidth();
			} else {
				tacc += w.getPreferredHeight();
			}
			w.setBounds(w.getPreferredWidth(), w.getPreferredHeight());
		}

		int ea = 0;
		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			ea = this.getWidth() - tacc;
		} else {
			ea = this.getHeight() - tacc;
		}
		if (this.expandChild != null && ea > 0) {
			int exPw, exPh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				exPh = expandChild.getPreferredHeight();
				if (exPh <= 0) {
					exPh = this.getHeight();
				}
				exPw = ea;
			} else {
				exPw = expandChild.getPreferredWidth();
				if (exPw <= 0) {
					exPw = this.getWidth();
				}
				exPh = ea;
			}
			this.expandChild.setBounds(exPw, exPh);
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

			List<Widget> wiz = new ArrayList<Widget>(this.leftChildren.size()
					+ 1 + this.rightChildren.size());
			wiz.addAll(this.leftChildren);
			if (drawExpanded && this.expandChild != null) {
				wiz.add(this.expandChild);
			}
			wiz.addAll(this.rightChildren);

			for (Widget widg : wiz) {
				if (this.orientation.equals(Orientation.HORIZONTAL) && acc > w) {
					break;
				} else if (this.orientation.equals(Orientation.HORIZONTAL)
						&& acc > h) {
					break;
				}
				GL11.glPushMatrix();
				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					GL11.glTranslatef(acc, 0, 0);
					Util.pushScissor(acc, h - widg.getHeight(),
							widg.getWidth(), widg.getHeight());
				} else {
					GL11.glTranslatef(0, acc, 0);
					Util.pushScissor(0, h - acc - widg.getHeight(),
							widg.getWidth(), widg.getHeight());
				}
				widg.render(alpha);
				Util.popScissor();
				GL11.glPopMatrix();
				Theme.getUiBgColor().use(alpha * Theme.getUiBgAlpha());
				GL11.glBegin(GL11.GL_QUADS);
				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					if (widg.getHeight() < h) {
						GL11.glVertex2i(acc, widg.getHeight());
						GL11.glVertex2i(acc + widg.getWidth(), widg.getHeight());
						GL11.glVertex2i(acc + widg.getWidth(), h);
						GL11.glVertex2i(acc, h);
					}
				} else {
					if (widg.getWidth() < w) {
						GL11.glVertex2i(widg.getWidth(), acc);
						GL11.glVertex2i(w, acc);
						GL11.glVertex2i(w, acc + widg.getHeight());
						GL11.glVertex2i(widg.getWidth(), acc + widg.getHeight());
					}

				}
				GL11.glEnd();

				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					acc += widg.getWidth();
				} else {
					acc += widg.getHeight();
				}
			}

		}
	}

	/**
	 * Append a widget to the container; will be added at the end of the left or
	 * top (depending the orientation) flow in fixed fill mode
	 * 
	 * @param w new widget to append to the left or top flow
	 */
	public void addBefore(Widget w) {
		super.add(w);
		this.leftChildren.add(w);
		resize();
	}

	/**
	 * Append a widget to the container; will be added at the end of the right
	 * or bottom (depending the orientation) flow in fixed fill mode
	 * 
	 * @param w new widget to append to the right or bottom flow
	 */
	public void addAfter(Widget w) {
		super.add(w);
		this.rightChildren.add(w);
		resize();
	}

	/**
	 * @param w the Widget to put in expand mode between the left/top and
	 *            right/bottom flow
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
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				cw += w.getPreferredWidth();
			} else {
				cw = Math.max(cw, w.getPreferredWidth());
			}
		}
		if (this.expandChild != null) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				cw += this.expandChild.getPreferredWidth();
			} else {
				cw = Math.max(cw, expandChild.getPreferredWidth());
			}
		}
		for (Widget w : this.rightChildren) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				cw += w.getPreferredWidth();
			} else {
				cw = Math.max(cw, w.getPreferredWidth());
			}
		}
		return cw;
	}

	@Override
	public int getPreferredHeight() {
		int mh = 0;
		for (Widget w : this.leftChildren) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				mh = Math.max(mh, w.getPreferredHeight());
			} else {
				mh += w.getPreferredHeight();
			}
		}
		if (this.expandChild != null) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				mh = Math.max(mh, this.expandChild.getPreferredHeight());
			} else {
				mh += expandChild.getPreferredHeight();
			}
		}
		for (Widget w : this.rightChildren) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				mh = Math.max(mh, w.getPreferredHeight());
			} else {
				mh += w.getPreferredHeight();
			}
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
