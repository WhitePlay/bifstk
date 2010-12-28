package bifstk.wm.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Simple horizontal/vertical container
 * <p>
 * Vertical mode: expands children horizontally, stacks them vertically
 * 
 * 
 */
public class Box extends Container {

	/**
	 * Orientation of the widgets contained by the box: left to right if
	 * {@link #HORIZONTAL} , top to bottom if {@link #VERTICAL}
	 */
	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	/** orientation of the box: vertical or horizontal */
	private Orientation orientation;

	/** list of contained widgets */
	private List<Widget> children;

	/** dimension of the box */
	private Rectangle bounds = null;

	/** currently hovered widget */
	private Widget widgetHover;
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
	 * 
	 * @param orient orientation
	 */
	public Box(Orientation orient) {
		this.orientation = orient;
		this.children = new ArrayList<Widget>();
		this.bounds = new Rectangle();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {
		int w = this.getWidth();
		int h = this.getHeight();

		float[] c1 = uiBg.toArray(4, alpha * uiBgAlpha);

		if (!hasChildren()) {

			int[] v1 = {
					0, 0, w, 0, w, h, 0, h
			};
			Util.raster().draw2D(v1, c1, GL11.GL_QUADS);
		} else {
			int acc = 0;

			for (Widget widg : this.children) {

				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					Util.pushTranslate(acc, 0);
					Util.pushScissor(widg.getWidth(), widg.getHeight());
				} else {
					Util.pushTranslate(0, acc);
					Util.pushScissor(widg.getWidth(), widg.getHeight());
				}

				widg.render(alpha, uiBg, uiBgAlpha);

				Util.popTranslate();
				Util.popScissor();

				// if the width/height of this widget is smaller
				// than the visible width/height, fill the gap
				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					if (widg.getHeight() < h) {
						int[] v2 = {
								acc, widg.getHeight(), //
								acc + widg.getWidth(), widg.getHeight(), //
								acc + widg.getWidth(), h, //
								acc, h
						};
						Util.raster().draw2D(v2, c1, GL11.GL_QUADS);
					}
				} else {
					if (widg.getWidth() < w) {
						int[] v2 = {
								widg.getWidth(), acc, //
								w, acc, //
								w, acc + widg.getHeight(), //
								widg.getWidth(), acc + widg.getHeight()
						};
						Util.raster().draw2D(v2, c1, GL11.GL_QUADS);
					}
				}

				if (this.orientation.equals(Orientation.HORIZONTAL)) {
					acc += widg.getWidth();
				} else {
					acc += widg.getHeight();
				}
			}

			// if the cumulated width/height of all widgets is smaller
			// than the visible width/height, fill the gap
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				if (acc < w) {
					int[] v3 = {
							acc, 0, //
							acc, h, //
							w, h, //
							w, 0
					};
					Util.raster().draw2D(v3, c1, GL11.GL_QUADS);
				}
			} else {
				if (acc < h) {
					int[] v3 = {
							0, acc, //
							w, acc, //
							w, h, //
							0, h
					};
					Util.raster().draw2D(v3, c1, GL11.GL_QUADS);
				}
			}
		}

	}

	@Override
	public void add(Widget w) {
		this.children.add(w);
		super.add(w);
	}

	@Override
	public void removeChild(Widget w) {
		if (this.children.remove(w)) {
			w.setParent(null);
		}
	}

	@Override
	public boolean hasChildren() {
		return this.children.size() > 0;
	}

	@Override
	public void clearChildren() {
		for (Widget w : this.children) {
			w.setParent(null);
		}
		this.children.clear();
	}

	@Override
	public void setWidth(int w) {
		if (this.bounds.getWidth() != w) {
			this.bounds.setWidth(w);

			for (Widget widg : children) {
				widg.setWidth(widg.getPreferredWidth(w));
			}
		}
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);

			for (Widget widg : children) {
				widg.setHeight(widg.getPreferredHeight(h));
			}
		}
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);

			for (Widget widg : children) {
				widg.setWidth(widg.getPreferredWidth(w));
				widg.setHeight(widg.getPreferredHeight(h));
			}
		}
	}

	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	@Override
	public int getPreferredWidth(int max) {
		int ret = 0;
		if (this.orientation.equals(Orientation.HORIZONTAL)) {
			// return the sum of the preferred widths
			for (Widget w : this.children) {
				ret += w.getPreferredWidth(max - ret);
			}
		} else {
			// return the preferred width of the largest widget
			for (Widget w : this.children) {
				ret = Math.max(w.getPreferredWidth(max), ret);
			}
		}
		return ret;
	}

	@Override
	public int getPreferredHeight(int max) {
		int ret = 0;
		if (this.orientation.equals(Orientation.VERTICAL)) {
			// return the sum of the preferred heights
			for (Widget w : this.children) {
				ret += w.getPreferredHeight(max - ret);
			}
		} else {
			// return the preferred height of the highest widget
			for (Widget w : this.children) {
				ret = Math.max(w.getPreferredHeight(max), ret);
			}
		}
		return ret;
	}

	@Override
	public void mouseHover(int x, int y) {
		int acc = 0;

		for (Widget w : this.children) {
			if (this.orientation.equals(Orientation.HORIZONTAL)) {
				if (acc < x && x < acc + w.getWidth() && x < this.getWidth()
						&& 0 < y && y < w.getHeight() && y < this.getHeight()) {
					w.mouseHover(x - acc, y);
					if (widgetHover != null && !widgetHover.equals(w)) {
						widgetHover.mouseOut();
					}
					widgetHover = w;
					widgetHoverDecal = acc;
				}
				acc += w.getWidth();

			} else {
				if (0 < x && x < w.getWidth() && x < this.getWidth() && acc < y
						&& y < acc + w.getHeight() && y < this.getHeight()) {
					w.mouseHover(x, y - acc);
					if (widgetHover != null && !widgetHover.equals(w)) {
						widgetHover.mouseOut();
					}
					widgetHover = w;
					widgetHoverDecal = acc;
				}
				acc += w.getHeight();
			}
		}
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
