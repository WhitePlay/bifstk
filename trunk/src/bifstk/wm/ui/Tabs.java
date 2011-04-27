package bifstk.wm.ui;

import java.util.Iterator;
import java.util.LinkedList;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Rasterizer;
import bifstk.gl.Util;
import bifstk.wm.geom.Rectangle;

/**
 * Contains multiple widgets, displays only one
 * <p>
 * Each widget gets a title, clicking the label corresponding
 * each widget, or tab, makes it active
 * 
 *
 */
public class Tabs extends Container {

	private Rectangle bounds = null;

	private final static int border = 3;

	/** sorted tabs */
	private LinkedList<Tab> tabs = null;

	private static class Tab {
		Widget content = null;
		String title = null;

		Tab(Widget content, String title) {
			this.content = content;
			this.title = title;
		}
	}

	/** focused tab */
	private Tab activeTab = null;
	/** mouse hovered tab */
	private Tab hoverTab = null;
	/** true when the active tab is hovered */
	private boolean contentHover = false;

	/** true when LBM is down on the content */
	private boolean contentLeftMouseDown = false;
	/** true when RBM is down on the content */
	private boolean contentRightMouseDown = false;
	/** true when CBM is down on the content */
	private boolean contentCenterMouseDown = false;

	/**
	 * Default constructor
	 */
	public Tabs() {
		this.tabs = new LinkedList<Tab>();
		this.bounds = new Rectangle();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {

		int bh = this.getTabsHeight();
		int w = this.getWidth();
		int h = this.getHeight();
		float a = alpha * uiBgAlpha;

		int acc = border, activeAccL = 0, activeAccR = 0;

		/* border */
		Util.raster().fillQuad(0, 0, border, h, uiBg, a);
		Util.raster().fillQuad(border, bh, w - 2 * border, border, uiBg, a);
		Util.raster().fillQuad(w - border, bh, border, h - bh, uiBg, a);
		Util.raster().fillQuad(border, h - border, w - 2 * border, border,
				uiBg, a);

		for (Tab tab : this.tabs) {
			int tw = getTabWidth(tab.title);

			/* content */
			if (tab.equals(this.activeTab)) {
				Rasterizer.pushTranslate(border, bh + border);
				Rasterizer.pushScissor(w - 2 * border, h - bh - 2 * border);
				tab.content.render(alpha, uiBg, uiBgAlpha);
				Rasterizer.popScissor();
				Rasterizer.popTranslate();
				activeAccL = acc;
				activeAccR = acc + tw;
			}

			/* tab bar */
			if (acc > w)
				continue;

			Color fontCol = uiBg.add(Theme.get().uiFontMask);
			if (tab.equals(this.activeTab)) {
				Util.raster().fillQuad(acc + 1, 0, tw - 2, bh,
						uiBg.add(Theme.get().uiTabFocusedHighlightMask), uiBg,
						a, a);
			} else {
				Util.raster()
						.fillQuad(
								acc + 1,
								0,
								tw - 2,
								bh,
								uiBg,
								uiBg.add(Theme.get().uiTabUnfocusedHighlightMask),
								a, a);

				Color hl = uiBg.add(Theme.get().uiTabUnfocusedBorderMask);
				fontCol = uiBg.add(Theme.get().uiTabUnfocusedFontMask);

				Util.raster().fillQuad(acc + 1, 0, tw - 2, 1, hl, a);
				Util.raster().fillQuad(acc + tw - 2, 1, 1, bh - 1, hl, a);
				Util.raster().fillQuad(acc + 1, 1, 1, bh - 1, hl, a);
			}

			Util.raster().fillQuad(acc, 0, 1, bh, uiBg, a);
			Util.raster().fillQuad(acc + tw - 1, 0, 1, bh, uiBg, a);

			Fonts.getNormal().drawString(acc + 4, 2, tab.title, fontCol, alpha);

			acc += tw;
		}

		if (acc < w) {
			// space right to the tabs
			Util.raster().fillQuad(acc, 0, w - acc, bh, uiBg, a);
		}

		/* content and active tab hightlight */
		Color hl = uiBg.add(Theme.get().uiBorderMask);
		Util.raster().fillQuad(0, bh, 1, h - bh, hl, a);
		Util.raster().fillQuad(w - 1, bh, 1, h - bh, hl, a);
		Util.raster().fillQuad(1, h - 1, w - 2, 1, hl, a);

		Util.raster().fillQuad(1, bh, activeAccL + 1, 1, hl, a);
		Util.raster()
				.fillQuad(activeAccR - 2, bh, w - activeAccR + 1, 1, hl, a);
		Util.raster().fillQuad(activeAccR - 2, 0, 1, bh, hl, a);
		Util.raster().fillQuad(activeAccL + 1, 0, 1, bh, hl, a);
		Util.raster().fillQuad(activeAccL + 2, 0, activeAccR - activeAccL - 4,
				1, hl, a);
	}

	@Override
	public void removeChild(Widget w) {
		for (Iterator<Tab> it = this.tabs.iterator(); it.hasNext();) {
			Tab t = it.next();
			if (t.content.equals(w)) {
				it.remove();
				t.content.setParent(null);
				if (this.activeTab == t) {
					if (this.tabs.isEmpty()) {
						this.activeTab = null;
					} else {
						this.activeTab = this.tabs.element();
					}
				}
			}
		}
	}

	@Override
	public boolean hasChildren() {
		return !tabs.isEmpty();
	}

	@Override
	public void clearChildren() {
		for (Tab t : this.tabs) {
			t.content.setParent(null);
		}
		this.tabs.clear();
		this.activeTab = null;
	}

	/**
	 * Add a widget as a new tab
	 * 
	 * @param tab
	 *            the widget to add
	 */
	public void addTab(Widget tab, String title) {
		if (tab != null) {
			Tab t = new Tab(tab, title);
			this.tabs.add(t);
			this.add(tab);
			if (this.activeTab == null)
				this.activeTab = t;
		}
	}

	@Override
	protected void update() {
	}

	/**
	 * Size of this changed, need to resize tabs
	 */
	private void resize() {
		int bh = this.getTabsHeight();
		int w = Math.max(0, this.getWidth() - 2 * border);
		int h = Math.max(0, this.getHeight() - bh - 2 * border);

		for (Tab tab : this.tabs) {
			tab.content.setBounds(w, h);
		}
	}

	@Override
	public void setWidth(int w) {
		if (this.bounds.getWidth() != w) {
			this.bounds.setWidth(w);
			resize();
		}
	}

	@Override
	public void setHeight(int h) {
		if (this.bounds.getHeight() != h) {
			this.bounds.setHeight(h);
			resize();
		}
	}

	@Override
	public void setBounds(int w, int h) {
		if (this.bounds.getWidth() != w || this.bounds.getHeight() != h) {
			this.bounds.setBounds(w, h);
			resize();
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
		if (this.hasChildren()) {
			int m = 0;
			int taken = 2 * border;
			for (Tab t : this.tabs) {
				m = Math.max(m, t.content.getPreferredWidth(max - taken));
			}
			m += taken;
			return Math.max(max, m);
		} else {
			return 0;
		}
	}

	@Override
	public int getPreferredHeight(int max) {
		if (this.hasChildren()) {
			int m = 0;
			int taken = getTabsHeight() + 2 * border;
			for (Tab t : this.tabs) {
				m = Math.max(m, t.content.getPreferredHeight(max - taken));
			}
			m += taken;
			return Math.max(max, m);
		} else {
			return 0;
		}
	}

	/**
	 * @return the pixel height of the tab bar
	 */
	private int getTabsHeight() {
		return Fonts.getNormal().getHeight() + 4;
	}

	/**
	 * @param title
	 *            the title of a tab
	 * @return the width of the corresponding tab
	 */
	private int getTabWidth(String title) {
		return Fonts.getNormal().getWidth(title) + 10;
	}

	@Override
	public void mouseHover(int x, int y) {
		if (border < x && x < getWidth() - border
				&& border + getTabsHeight() < y && y < getHeight() - border) {
			if (this.activeTab != null) {
				this.activeTab.content.mouseHover(x - border, y - border
						- getTabsHeight());
				this.contentHover = true;
			}
		} else if (this.contentHover) {
			this.contentHover = false;
			if (this.hoverTab != null) {
				this.hoverTab.content.mouseOut();
				this.hoverTab = null;
			}
		}

		if (x > border && y < getTabsHeight()) {
			this.hoverTab = null;
			int acc = border;
			for (Tab tab : tabs) {
				int tw = getTabWidth(tab.title);
				if (acc < x && x <= acc + tw) {
					this.hoverTab = tab;
				}
				acc += tw;
			}
		}
	}

	@Override
	public void mouseOut() {
		if (this.activeTab != null) {
			this.activeTab.content.mouseOut();
		}
		this.contentHover = false;
	}

	@Override
	public void mouseDown(int button) {
		if (this.contentHover) {
			if (!contentLeftMouseDown && button == 0) {
				this.contentLeftMouseDown = true;
			} else if (!contentRightMouseDown && button == 1) {
				this.contentRightMouseDown = true;
			} else if (!contentCenterMouseDown && button == 2) {
				this.contentCenterMouseDown = true;
			}

			if (this.activeTab != null) {
				this.activeTab.content.mouseDown(button);
			}
		} else if (this.hoverTab != null) {
			this.activeTab = hoverTab;
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		boolean hadOne = false;
		if (this.contentLeftMouseDown && button == 0) {
			this.contentLeftMouseDown = false;
			hadOne = true;
		} else if (this.contentRightMouseDown && button == 1) {
			this.contentRightMouseDown = false;
			hadOne = true;
		} else if (this.contentCenterMouseDown && button == 2) {
			this.contentCenterMouseDown = false;
			hadOne = true;
		}

		if (hadOne && this.activeTab != null) {
			this.activeTab.content.mouseUp(button, x - border, y - border
					* getTabsHeight());
		}
	}
}
