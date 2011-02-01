package bifstk.wm.ui;

import java.util.ArrayList;
import java.util.List;

import bifstk.gl.Color;
import bifstk.gl.Rasterizer;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Container that stacks Widgets horizontally
 * <p>
 * Depending which method is used to add a Widget in the FlowBox, Widgets are
 * laid out as follows:
 * 
 * <pre>
 * FlowBox b = new FlowBox(Orientation.HORIZONTAL);
 * b.addBefore(w1);
 * b.addAfter(w2);
 * b.setExpand(w3);
 * b.addBefore(w4);
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
 * <p>
 * FlowBox consumes all available vertical/horizontal space UNLESS components
 * have only been added using {@link #addBegin(Widget)}.
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

	/** true when mouse is hovering this flowbox */
	private boolean mouseHover = false;
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

	/** the bound button or null */
	private AbstractButton boundButton = null;

	/** true if expandChild has room to be rendered */
	private boolean drawExpanded = false;
	/** the size of the expand space as last computed in #resize() */
	private int expandWidth = 0;

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

	@Override
	protected void update() {
		resize();
	}

	/**
	 * Called when the geometry of the box has changed: need to subsequently
	 * resize children widgets
	 */
	private void resize() {
		int w = this.getWidth();
		int h = this.getHeight();
		expandWidth = 0;
		int tacc = 0;

		for (Widget widg : this.leftChildren) {
			int nw, nh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				nw = Util.clampi(widg.getPreferredWidth(w), 0, w - tacc);
				nh = Util.clampi(widg.getPreferredHeight(h), 0, h);
				tacc += nw;
			} else {
				nw = Util.clampi(widg.getPreferredWidth(w), 0, w);
				nh = Util.clampi(widg.getPreferredHeight(h), 0, h - tacc);
				tacc += nh;
			}
			tacc += borderWidth;
			widg.setBounds(nw, nh);
		}

		for (Widget widg : this.rightChildren) {
			int nw, nh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				nw = Util.clampi(widg.getPreferredWidth(w), 0, w - tacc);
				nh = Util.clampi(widg.getPreferredHeight(h), 0, h);
				tacc += nw;
			} else {
				nw = Util.clampi(widg.getPreferredWidth(w), 0, w);
				nh = Util.clampi(widg.getPreferredHeight(h), 0, h - tacc);
				tacc += nh;
			}
			widg.setBounds(nw, nh);
		}
		if (this.rightChildren.size() > 0)
			tacc += (this.rightChildren.size() - 1) * this.borderWidth;

		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			expandWidth = Math.max(0, w - tacc);
		} else {
			expandWidth = Math.max(0, h - tacc);
		}
		if (this.expandChild != null && expandWidth > 0) {
			expandWidth = Math.max(0, expandWidth - borderWidth);
			int exPw, exPh;
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				exPh = Util.clampi(expandChild.getPreferredHeight(h), 0, h);
				if (exPh <= 0) {
					exPh = h;
				}
				exPw = expandWidth;
			} else {
				exPw = Util.clampi(expandChild.getPreferredWidth(w), 0, w);
				if (exPw <= 0) {
					exPw = w;
				}
				exPh = expandWidth;
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

		if (this.boundButton != null && this.mouseHover) {
			uiBg = uiBg.highlight();
		}

		if (!hasChildren()) {
			Util.raster().fillQuad(0, 0, w, h, uiBg, alpha * uiAlpha);
		} else {
			int acc = 0, i = 0;

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
				i++;

				if (this.orientation.equals(Orientation.HORIZONTAL) && acc > w) {
					break;
				} else if (this.orientation.equals(Orientation.VERTICAL)
						&& acc > h) {
					break;
				}
				if (widg == null) {
					if (this.orientation.equals(Orientation.HORIZONTAL)) {
						Util.raster().fillQuad(acc, 0, expandWidth, h, uiBg,
								uiAlpha * alpha);
					} else {
						Util.raster().fillQuad(0, acc, w, expandWidth, uiBg,
								uiAlpha * alpha);
					}

					acc += expandWidth;
				} else {
					if (this.orientation.equals(Orientation.HORIZONTAL)) {
						Rasterizer.pushTranslate(acc, 0);
						Rasterizer.pushScissor(widg.getWidth(),
								widg.getHeight());
					} else {
						Rasterizer.pushTranslate(0, acc);
						Rasterizer.pushScissor(widg.getWidth(),
								widg.getHeight());
					}
					widg.render(alpha, uiBg, uiAlpha);

					Rasterizer.popScissor();
					Rasterizer.popTranslate();

					if (this.orientation.equals(Orientation.HORIZONTAL)) {
						if (widg.getHeight() < h) {
							Util.raster().fillQuad(acc, widg.getHeight(),
									widg.getWidth(), h - widg.getHeight(),
									uiBg, uiAlpha * alpha);
						}
						acc += widg.getWidth();
					} else {
						if (widg.getWidth() < w) {
							Util.raster().fillQuad(widg.getWidth(), acc,
									w - widg.getWidth(), widg.getHeight(),
									uiBg, uiAlpha * alpha);
						}
						acc += widg.getHeight();
					}

					if (i != wiz.size()) {
						if (this.orientation.equals(Orientation.HORIZONTAL)) {
							Util.raster().fillQuad(acc, 0, borderWidth, h,
									uiBg, uiAlpha * alpha);
						} else {
							Util.raster().fillQuad(0, acc, w, borderWidth,
									uiBg, uiAlpha * alpha);
						}
						acc += this.borderWidth;
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
	 * @throws IllegalStateException if this FlowBox has an AttachedButton and
	 *             <code>w</code> is a Container
	 */
	public void addBegin(Widget w) {
		if (w == null) {
			return;
		}
		if (this.boundButton != null
				&& Container.class.isAssignableFrom(w.getClass())) {
			throw new IllegalStateException(
					"Cannot insert a Container as this FlowBox has a bound Button");
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
	 * @throws IllegalStateException if this FlowBox has an AttachedButton and
	 *             <code>w</code> is a Container
	 */
	public void addEnd(Widget w) {
		if (w == null) {
			return;
		}
		if (this.boundButton != null
				&& Container.class.isAssignableFrom(w.getClass())) {
			throw new IllegalStateException(
					"Cannot insert a Container as this FlowBox has a bound Button");
		}

		super.add(w);
		this.rightChildren.add(w);
		resize();
	}

	/**
	 * @param w the Widget to put in expand mode between the left/top and
	 *            right/bottom flow; can be null
	 * @throws IllegalStateException if this FlowBox has an AttachedButton and
	 *             <code>w</code> is a Container
	 */
	public void setExpand(Widget w) {
		super.add(w);
		if (this.expandChild != null) {
			this.expandChild.setParent(null);
		}
		if (this.boundButton != null
				&& Container.class.isAssignableFrom(w.getClass())) {
			throw new IllegalStateException(
					"Cannot insert a Container as this FlowBox has a bound Button");
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
	public int getPreferredWidth(int max) {
		int cw = 0;
		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			boolean expand = false;
			int lc = 0, rc = 0;

			for (Widget w : this.leftChildren) {
				cw += w.getPreferredWidth(max - cw);
				lc++;
			}
			if (this.expandChild != null) {
				cw += this.expandChild.getPreferredWidth(max - cw);
				expand = true;
			}
			for (Widget w : this.rightChildren) {
				cw += w.getPreferredWidth(max - cw);
				expand = true;
				rc++;
			}
			if (rc > 0)
				rc--;
			cw += this.borderWidth * (lc + rc);

			if (expand)
				cw = Math.max(cw, max);
		} else {
			for (Widget w : this.leftChildren) {
				cw = Math.max(cw, w.getPreferredWidth(max));
			}
			if (this.expandChild != null) {
				cw = Math.max(cw, this.expandChild.getPreferredWidth(max));
			}
			for (Widget w : this.rightChildren) {
				cw = Math.max(cw, w.getPreferredWidth(max));
			}
		}

		return cw;
	}

	@Override
	public int getPreferredHeight(int max) {
		int mh = 0;
		if (this.orientation.equals(Orientation.VERTICAL)) {
			boolean expand = false;
			int lc = 0, rc = 0;

			for (Widget w : this.leftChildren) {
				mh += w.getPreferredHeight(max - mh);
				lc++;
			}
			if (this.expandChild != null) {
				mh += this.expandChild.getPreferredHeight(max - mh);
				expand = true;
			}
			for (Widget w : this.rightChildren) {
				mh += w.getPreferredHeight(max - mh);
				expand = true;
				rc++;
			}
			if (rc > 0)
				rc--;
			mh += this.borderWidth * (lc + rc);

			if (expand)
				mh = Math.max(mh, max);
		} else {
			for (Widget w : this.leftChildren) {
				mh = Math.max(mh, w.getPreferredHeight(max));
			}
			if (this.expandChild != null) {
				mh = Math.max(mh, this.expandChild.getPreferredHeight(max));
			}
			for (Widget w : this.rightChildren) {
				mh = Math.max(mh, w.getPreferredHeight(max));
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

	/**
	 * Binding a Button to a Container means that mouse events sent to this
	 * FlowBox will be redirected to the bound button.
	 * <p>
	 * Useful when clicking the label attached to a radio/check button activates
	 * that button
	 * <p>
	 * This button has to be contained in this FlowBox
	 * <p>
	 * This FlowBox cannot contain other Containers if it has a bound button
	 * 
	 * @param button the Button to attach
	 * @throws NullPointerException argument is null
	 * @throws IllegalStateException this FlowBox already contains a Container;
	 *             this FlowBox does not contain <code>button</code>
	 */
	public void bindButton(AbstractButton button) {
		if (button == null)
			throw new NullPointerException("Argument cannot be null");

		boolean found = false;
		for (Widget w : this.leftChildren) {
			if (w.equals(button))
				found = true;
			if (Container.class.isAssignableFrom(w.getClass()))
				throw new IllegalStateException(
						"Cannot attach a Button to a FlowBox that contains a Container");
		}

		if (this.expandChild != null) {
			if (expandChild.equals(button))
				found = true;
			if (Container.class.isAssignableFrom(expandChild.getClass()))
				throw new IllegalStateException(
						"Cannot attach a Button to a FlowBox that contains a Container");
		}

		for (Widget w : this.rightChildren) {
			if (w.equals(button))
				found = true;
			if (Container.class.isAssignableFrom(w.getClass()))
				throw new IllegalStateException(
						"Cannot attach a Button to a FlowBox that contains a Container");
		}

		if (!found)
			throw new IllegalStateException(
					"The bound Button must be contained in this FlowBox");

		this.boundButton = button;
	}

	/**
	 * @return the Button bound to this FlowBox, or null
	 */
	public AbstractButton getAttachedButton() {
		return this.boundButton;
	}

	@Override
	public void mouseHover(int x, int y) {
		this.mouseHover = true;
		if (this.boundButton == null) {
			int acc = 0;
			for (Widget wid : this.leftChildren) {
				acc = _mouseHover(wid, acc, x, y);
				if (acc < 0)
					return;
				acc += this.borderWidth;
			}

			if (this.expandChild != null) {
				acc = _mouseHover(expandChild, acc, x, y);
				if (acc < 0)
					return;
				acc += this.borderWidth;
			} else {
				acc += this.expandWidth;
			}

			for (Widget wid : this.rightChildren) {
				acc = _mouseHover(wid, acc, x, y);
				if (acc < 0)
					return;
				acc += this.borderWidth;
			}
			if (widgetHover != null) {
				this.widgetHover.mouseOut();
				widgetHover = null;
			}
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
		this.mouseHover = false;
	}

	@Override
	public void mouseDown(int button) {
		if (this.boundButton != null) {
			this.boundButton.mouseDown(button);
		} else {
			if (this.widgetHover != null) {
				this.widgetHover.mouseDown(button);
				if (this.widgetLeftMouseDown == null && button == 0) {
					this.widgetLeftMouseDownDecal = this.widgetHoverDecal;
					this.widgetLeftMouseDown = this.widgetHover;
					this.widgetLeftMouseDown.mouseDown(button);
				} else if (this.widgetRightMouseDown == null && button == 1) {
					this.widgetRightMouseDownDecal = this.widgetHoverDecal;
					this.widgetRightMouseDown = this.widgetHover;
					this.widgetRightMouseDown.mouseDown(button);
				} else if (this.widgetCenterMouseDown == null && button == 2) {
					this.widgetCenterMouseDownDecal = this.widgetHoverDecal;
					this.widgetCenterMouseDown = this.widgetHover;
					this.widgetCenterMouseDown.mouseDown(button);
				}
			}
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		if (this.boundButton != null) {
			// fake event coords: 1,1 so the button thinks the event was legit
			this.boundButton.mouseUp(button, 1, 1);
		} else {
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

}
