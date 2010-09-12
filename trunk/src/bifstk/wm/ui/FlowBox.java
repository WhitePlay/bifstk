package bifstk.wm.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import bifstk.gl.Color;
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

	/** widget currently hovered by mouse */
	private Widget widgetHover = null;
	/** horizontal/vertical offset of the hovered widget */
	private int widgetHoverDecal = 0;

	/** widget LMB is clicking */
	private Widget widgetLeftMouseDown = null;
	/** hor/vert offset of the widget clicked by LMB */
	private int widgetLeftMouseDownDecal = 0;

	/** widget LMB is clicking */
	private Widget widgetRightMouseDown = null;
	/** hor/vert offset of the widget clicked by LMB */
	private int widgetRightMouseDownDecal = 0;

	/** widget LMB is clicking */
	private Widget widgetCenterMouseDown = null;
	/** hor/vert offset of the widget clicked by LMB */
	private int widgetCenterMouseDownDecal = 0;

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
	private int ew = 0;

	/**
	 * Called when the geometry of the box has changed: need to subsequently
	 * resize children widgets
	 */
	private void resize() {
		int w = this.getWidth();
		int h = this.getHeight();
		ew = 0;
		int tacc = 0;

		for (Widget widg : this.leftChildren) {
			int nw, nh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				nw = Util.clampi(widg.getPreferredWidth(), 0, w - tacc);
				nh = Util.clampi(widg.getPreferredHeight(), 0, h);
				tacc += nw;
			} else {
				nw = Util.clampi(widg.getPreferredWidth(), 0, w);
				nh = Util.clampi(widg.getPreferredHeight(), 0, h - tacc);
				tacc += nh;
			}
			widg.setBounds(nw, nh);
		}

		for (Widget widg : this.rightChildren) {
			int nw, nh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				nw = Util.clampi(widg.getPreferredWidth(), 0, w - tacc);
				nh = Util.clampi(widg.getPreferredHeight(), 0, h);
				tacc += nw;
			} else {
				nw = Util.clampi(widg.getPreferredWidth(), 0, w);
				nh = Util.clampi(widg.getPreferredHeight(), 0, h - tacc);
				tacc += nh;
			}
			widg.setBounds(nw, nh);
		}

		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			ew = Math.max(0, w - tacc);
		} else {
			ew = Math.max(0, h - tacc);
		}
		if (this.expandChild != null && ew > 0) {
			int exPw, exPh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				exPh = Util.clampi(expandChild.getPreferredHeight(), 0, h);
				if (exPh <= 0) {
					exPh = h;
				}
				exPw = ew;
			} else {
				exPw = Util.clampi(expandChild.getPreferredWidth(), 0, w);
				if (exPw <= 0) {
					exPw = w;
				}
				exPh = ew;
			}
			this.expandChild.setBounds(exPw, exPh);
			this.drawExpanded = true;
		} else {
			this.drawExpanded = false;
		}
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {
		int w = this.getWidth();
		int h = this.getHeight();

		if (!hasChildren()) {
			uiBg.use(alpha * uiAlpha);
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
			} else {
				wiz.add(null);
			}
			wiz.addAll(this.rightChildren);

			for (Widget widg : wiz) {
				if (this.orientation.equals(Orientation.HORIZONTAL) && acc > w) {
					break;
				} else if (this.orientation.equals(Orientation.VERTICAL)
						&& acc > h) {
					break;
				}
				if (widg == null) {
					uiBg.use(alpha * uiAlpha);
					GL11.glBegin(GL11.GL_QUADS);
					if (this.orientation.equals(Orientation.HORIZONTAL)) {
						GL11.glVertex2i(acc, 0);
						GL11.glVertex2i(acc + ew, 0);
						GL11.glVertex2i(acc + ew, h);
						GL11.glVertex2i(acc, h);
					} else {
						GL11.glVertex2i(0, acc);
						GL11.glVertex2i(0, acc + ew);
						GL11.glVertex2i(w, acc + ew);
						GL11.glVertex2i(w, acc);
					}
					GL11.glEnd();

					acc += ew;
				} else {
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
					widg.render(alpha, uiBg, uiAlpha);
					Util.popScissor();
					GL11.glPopMatrix();
					uiBg.use(alpha * uiAlpha);
					GL11.glBegin(GL11.GL_QUADS);
					if (this.orientation.equals(Orientation.HORIZONTAL)) {
						if (widg.getHeight() < h) {
							GL11.glVertex2i(acc, widg.getHeight());
							GL11.glVertex2i(acc + widg.getWidth(),
									widg.getHeight());
							GL11.glVertex2i(acc + widg.getWidth(), h);
							GL11.glVertex2i(acc, h);
						}
					} else {
						if (widg.getWidth() < w) {
							GL11.glVertex2i(widg.getWidth(), acc);
							GL11.glVertex2i(w, acc);
							GL11.glVertex2i(w, acc + widg.getHeight());
							GL11.glVertex2i(widg.getWidth(),
									acc + widg.getHeight());
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
	}

	/**
	 * Append a widget to the container; will be added at the end of the left or
	 * top (depending the orientation) flow in fixed fill mode
	 * 
	 * @param w new widget to append to the left or top flow
	 */
	public void addBefore(Widget w) {
		if (w == null) {
			return;
		}
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
		if (w == null) {
			return;
		}
		super.add(w);
		this.rightChildren.add(w);
		resize();
	}

	/**
	 * @param w the Widget to put in expand mode between the left/top and
	 *            right/bottom flow; can be null
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
		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			return Integer.MAX_VALUE / 2;
		}

		int cw = 0;
		for (Widget w : this.leftChildren) {
			cw = Math.max(cw, w.getPreferredWidth());
		}
		if (this.expandChild != null) {
			cw += this.expandChild.getPreferredWidth();
		}
		for (Widget w : this.rightChildren) {
			cw = Math.max(cw, w.getPreferredWidth());
		}
		return cw;
	}

	@Override
	public int getPreferredHeight() {
		if (this.orientation.equals(Orientation.VERTICAL)) {
			return Integer.MAX_VALUE / 2;
		}

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

	@Override
	public void mouseHover(int x, int y) {
		int acc = 0;
		for (Widget wid : this.leftChildren) {
			acc = _mouseHover(wid, acc, x, y);
			if (acc < 0)
				return;
		}

		acc += this.ew;
		if (this.expandChild != null) {
			acc = _mouseHover(expandChild, acc, x, y);
			if (acc < 0)
				return;
		}

		for (Widget wid : this.rightChildren) {
			acc = _mouseHover(wid, acc, x, y);
			if (acc < 0)
				return;
		}
		if (widgetHover != null) {
			this.widgetHover.mouseOut();
			widgetHover = null;
		}
	}

	/**
	 * Internal mouseHover
	 */
	private int _mouseHover(Widget wid, int acc, int x, int y) {
		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			if (acc < x && x < acc + wid.getWidth() && x < this.getWidth()
					&& 0 < y && y < wid.getHeight() && y < this.getHeight()) {
				wid.mouseHover(x - acc, y);
				if (widgetHover != null && !widgetHover.equals(wid)) {
					widgetHover.mouseOut();
				}
				widgetHover = wid;
				widgetHoverDecal = acc;
				return -1;
			}
			acc += wid.getWidth();
		} else {
			if (0 < x && x < wid.getWidth() && x < this.getWidth() && acc < y
					&& y < acc + wid.getHeight() && y < this.getHeight()) {
				wid.mouseHover(x, y - acc);
				if (widgetHover != null && !widgetHover.equals(wid)) {
					widgetHover.mouseOut();
				}
				widgetHover = wid;
				widgetHoverDecal = acc;
				return -1;
			}
			acc += wid.getHeight();
		}
		return acc;
	}

	@Override
	public void mouseOut() {
		if (this.widgetHover != null) {
			this.widgetHover.mouseOut();
			this.widgetHover = null;
		}
	}

	@Override
	public void mouseDown(int button) {
		if (this.widgetHover != null) {
			this.widgetHover.mouseDown(button);
			if (this.widgetLeftMouseDown == null && button == 0) {
				this.widgetLeftMouseDownDecal = this.widgetHoverDecal;
				this.widgetLeftMouseDown = this.widgetHover;
			} else if (this.widgetRightMouseDown == null && button == 1) {
				this.widgetRightMouseDownDecal = this.widgetHoverDecal;
				this.widgetRightMouseDown = this.widgetHover;
			} else if (this.widgetCenterMouseDown == null && button == 2) {
				this.widgetCenterMouseDownDecal = this.widgetHoverDecal;
				this.widgetCenterMouseDown = this.widgetHover;
			}
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		int offset = 0;
		Widget wid = null;
		if (this.widgetLeftMouseDown != null && button == 0) {
			wid = widgetLeftMouseDown;
			this.widgetLeftMouseDown = null;
			offset = this.widgetLeftMouseDownDecal;
		} else if (this.widgetRightMouseDown != null && button == 1) {
			wid = widgetRightMouseDown;
			this.widgetRightMouseDown = null;
			offset = this.widgetRightMouseDownDecal;
		} else if (this.widgetCenterMouseDown != null && button == 2) {
			wid = widgetCenterMouseDown;
			this.widgetCenterMouseDown = null;
			offset = this.widgetCenterMouseDownDecal;
		}
		if (wid != null) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				wid.mouseUp(button, x - offset, y);
			} else {
				wid.mouseUp(button, x, y - offset);
			}
		}

	}

}
