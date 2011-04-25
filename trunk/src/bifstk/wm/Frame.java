package bifstk.wm;

import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import bifstk.config.Config;
import bifstk.config.Fonts;
import bifstk.config.TextureLoader;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Rasterizer;
import bifstk.gl.Util;
import bifstk.wm.geom.Point;
import bifstk.wm.geom.Rectangle;
import bifstk.wm.geom.Region;
import bifstk.wm.ui.Focusable;
import bifstk.wm.ui.Widget;

/**
 * a Frame is a Window in the Window Manager
 * <p>
 * It has a title bar and contains widgets
 */
public abstract class Frame implements Drawable, Clickable {

	/** dimensions */
	private Rectangle bounds = null;

	/** position in the WM */
	private Point pos = null;

	/** backup bounds for switching back from maximized mode */
	private Rectangle windowedBounds = null;
	/** backup position for switching back from maximized mode */
	private Point windowedPos = null;

	/** minimum dimensions */
	private Rectangle minBounds = null;

	/** true if the frame is currently focused in the WM */
	private boolean focused = false;

	/** true if the frame is currently dragged in the WM */
	private boolean dragged = false;

	/** true if the frame is currently resized in the WM */
	private boolean resized = false;

	/** true if the frame is currently docked */
	private boolean docked = false;

	/** width of the corner in pixels for mouse corner resize */
	private final int cornerWidth = 15;

	/** title of the frame */
	private String title = "";
	/** frame has no titlebar if false */
	private boolean hasTitlebar = true;
	/** frame occupies all the space of the display */
	private boolean maximized = false;
	/** frame cannot be resized */
	private boolean resizable = true;

	/** Frame controls and elements to put in the titlebar */
	public static enum Controls {
		/** contains the title displayed in the titlebar */
		TITLE("Title"),
		/** control that closes the frame */
		CLOSE("Close"),
		/** control that toggles maximize / windowed */
		MAXIMIZE("Maximize");

		private String name;

		private Controls(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	};

	/** true if the Close Control button is hovered by the mouse */
	private boolean controlCloseHover = false;
	/** true if the Close Control button is clicked by the mouse */
	private boolean controlCloseDown = false;
	/** time when close control hover state changed */
	private long controlCloseHoverTime = 0;

	/** true if the Maximize Control button is hovered by the mouse */
	private boolean controlMaximizeHover = false;
	/** true if the Maximize Control button is clicked by the mouse */
	private boolean controlMaximizeDown = false;
	/** time when maximize control hover state changed */
	private long controlMaximizeHoverTime = 0;

	/** content of the frame */
	private Widget content = null;

	/** true if the content of the frame is hovered by the mouse */
	private boolean contentHover = false;
	/** true if the content is clicked by LMB */
	private boolean contentLeftMouseDown = false;
	/** true if the content is clicked by RMB */
	private boolean contentRightMouseDown = false;
	/** true if the content is clicked by CMB */
	private boolean contentCenterMouseDown = false;

	/** the last time the drag status changed */
	private long dragChangeTime = 0;
	/** the last time the focus status changed */
	private long focusChangeTime = 0;
	/** last time the resize status changed */
	private long resizeChangeTime = 0;
	/** time this Frame appeared in the WM */
	private long apparitionTime = 0;
	/** time this Frame was removed from the WM */
	private long removalTime = 0;

	/** false if this Frame is not part of the WM */
	private boolean active = false;

	/** Widget that currently has keyboard focus */
	private Focusable keyboardFocus = null;

	/**
	 * Default constructor
	 * 
	 * @param x abscissa in the WM
	 * @param y ordinate in the WM
	 */
	public Frame(int x, int y) {
		this(x, y, 100, 100);
	}

	/**
	 * Default constructor
	 * 
	 * @param x abscissa in the WM
	 * @param y ordinate in the WM
	 * @param w width
	 * @param h height
	 */
	public Frame(int x, int y, int w, int h) {
		this.bounds = new Rectangle(w, h);
		int min = Config.get().getWmFrameSizeMin();
		this.minBounds = new Rectangle(min, min);
		this.pos = new Point(x, y);
	}

	/**
	 * Called by the state when this Frame is added to the WM
	 */
	public void init() {
		this.apparitionTime = Sys.getTime();
		this.active = true;
	}

	/**
	 * Called by the state when this Frame is removed from the WM
	 */
	public void teardown() {
		this.removalTime = Sys.getTime();
		this.active = false;
	}

	/**
	 * @return true if this Frame is part of the WM
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @return true if this Frame is not active and not visible
	 */
	public boolean isRemovable() {
		return !this.active
				&& (Sys.getTime() - this.removalTime) > Config.get()
						.getWmAnimationsLength();
	}

	/** {@inheritDoc} */
	@Override
	public void render(float alpha, Color uiColor, float uiAlpha) {
		int x, y, w, h;
		float alpha2 = alpha * uiAlpha;
		int borderWidth = getBorderWidth();
		int titlebarHeight = getTitleBarHeight();
		long t = Sys.getTime();

		x = this.getX();
		y = this.getY();
		w = this.getWidth();
		h = this.getHeight();

		float focusAnim = getFocusAnim();

		Color borderCol = getBorderFocusedColor().blend(
				getBorderUnfocusedColor(), focusAnim);
		Color borderBorderCol = getBorderOuterFocusedColor().blend(
				getBorderOuterUnfocusedColor(), focusAnim);

		// border
		Util.raster().fillQuad(x, y, w, borderWidth, borderCol, alpha2);
		Util.raster().fillQuad(x, y + borderWidth, borderWidth,
				h - 2 * borderWidth, borderCol, alpha2);
		Util.raster().fillQuad(x + w - borderWidth, y + borderWidth,
				borderWidth, h - 2 * borderWidth, borderCol, alpha2);
		Util.raster().fillQuad(x, y + h - borderWidth, w, borderWidth,
				borderCol, alpha2);

		if (borderWidth > 1) {
			// metaborder
			Util.raster().drawQuad(x, y, w, h, borderBorderCol, alpha2);
		}

		// title-bar
		if (this.hasTitlebar) {

			// background
			Util.raster().fillQuad(x + borderWidth, y + borderWidth,
					w - borderWidth * 2, titlebarHeight, borderCol, alpha2);

			Color titleCol2 = getFrameTitlebarFocusedColor().blend(
					getFrameTitlebarUnfocusedColor(), focusAnim);

			// background
			int dec = 1;
			if (borderWidth == 0) {
				dec = 0;
			}

			Util.raster().fillQuad(x + dec, y + dec, w - dec * 2,
					titlebarHeight + borderWidth - dec, titleCol2, titleCol2,
					alpha2, 0.0f);

			int controlWidth = Theme.get().frameControlsWidth;
			int controlHeight = Theme.get().frameControlsHeight;
			int controlBorder = Theme.get().frameControlsBorder;
			int spaceLeft = w - 2 * borderWidth;
			int acc = 0;
			int controlsNum = 2;

			if (!this.isResizable()) {
				controlsNum = 1;
			}

			// title & controls
			List<Controls> controls = Theme.get().frameControlsOrder;
			for (Controls c : controls) {
				if (c.equals(Controls.TITLE)) {
					int titleWidth = spaceLeft - controlsNum
							* (controlWidth + controlBorder);

					Rasterizer.pushTranslate(x + borderWidth + acc, y
							+ borderWidth);
					Rasterizer.pushScissor(titleWidth, titlebarHeight);

					Fonts.getNormal().drawString(0, 0, this.title,
							Theme.get().uiFontColor, alpha);

					Rasterizer.popScissor();
					Rasterizer.popTranslate();

					acc += titleWidth + controlBorder;
				} else {
					bifstk.gl.Image img = null;
					Color col = null;
					int yClickDec = 0;
					boolean hover = false;
					float hoverAnim = 0.0f;
					long hoverAnimLen = Config.get().getWmAnimationsLength();

					switch (c) {
					case CLOSE:
						img = TextureLoader.getWindowClose();
						if (this.controlCloseDown && this.controlCloseHover) {
							col = Theme.get().frameControlsCloseClickColor;
							yClickDec = 1;
						} else if (this.controlCloseHover) {
							col = Theme.get().frameControlsCloseHoverColor;
							hover = true;
							hoverAnim = Util.clampf(
									(float) (t - this.controlCloseHoverTime)
											/ (float) hoverAnimLen, 0.0f, 1.0f);
						} else {
							col = Theme.get().frameControlsCloseColor
									.blend(Theme.get().frameControlsCloseUnfocusedColor,
											focusAnim);
							if (!this.controlCloseDown) {
								hoverAnim = 1.0f - Util
										.clampf((float) (t - this.controlCloseHoverTime)
												/ (float) hoverAnimLen, 0.0f,
												1.0f);
							}
						}
						break;
					case MAXIMIZE:
						if (this.isResizable()) {
							img = TextureLoader.getWindowMaximize();
							if (this.controlMaximizeDown
									&& this.controlMaximizeHover) {
								col = Theme.get().frameControlsMaximizeClickColor;
								yClickDec = 1;
							} else if (this.controlMaximizeHover) {
								col = Theme.get().frameControlsMaximizeHoverColor;
								hover = true;
								hoverAnim = Util
										.clampf((float) (t - this.controlMaximizeHoverTime)
												/ (float) hoverAnimLen, 0.0f,
												1.0f);
							} else {
								col = Theme.get().frameControlsMaximizeColor
										.blend(Theme.get().frameControlsMaximizeUnfocusedColor,
												focusAnim);
								if (!this.controlMaximizeDown) {
									hoverAnim = 1.0f - Util
											.clampf((float) (t - this.controlMaximizeHoverTime)
													/ (float) hoverAnimLen,
													0.0f, 1.0f);
								}
							}
						}
						break;
					}
					if (img != null) {
						Rasterizer.pushTranslate(x + borderWidth + acc, y
								+ borderWidth);
						Rasterizer.pushScissor(controlWidth, controlHeight);

						Util.raster().fillQuad(0, yClickDec, img, col, alpha);

						Rasterizer.popScissor();
						Rasterizer.popTranslate();

						if (!Config.get().isWmAnimations()) {
							hoverAnim = (hover ? 1.0f : 0.0f);
						}

						if (hover || hoverAnim > 0.0f) {
							Util.drawShadowQuad(x + borderWidth + acc, y
									+ yClickDec + borderWidth, img.getWidth(),
									img.getHeight(), 0.2f * hoverAnim, col,
									true);
							Util.raster().fillQuad(x + borderWidth + acc,
									y + yClickDec + borderWidth,
									img.getWidth(), img.getHeight(), col,
									0.2f * hoverAnim);

						}

						acc += controlWidth + controlBorder;
						spaceLeft -= controlWidth + controlBorder;
						controlsNum--;
					}
				}
			}
		}

		// content
		if (this.content == null) {
			Util.raster().drawQuad(x + borderWidth,
					y + titlebarHeight + borderWidth, w - borderWidth * 2,
					h - titlebarHeight - 2 * borderWidth, uiColor, alpha2);
		} else {
			Rasterizer.pushTranslate(x + borderWidth, y + titlebarHeight
					+ borderWidth);
			Rasterizer.pushScissor(w - 2 * borderWidth, h - 2 * borderWidth
					- titlebarHeight);

			this.content.render(alpha, uiColor, uiAlpha);

			Rasterizer.popScissor();
			Rasterizer.popTranslate();
		}
	}

	/** @return the abscissa of the top left corner of this frame */
	public int getX() {
		return this.pos.getX();
	}

	/** @return the ordinate of the top left corner of this frame */
	public int getY() {
		return this.pos.getY();
	}

	/** @param x the new abscissa position of this frame in the WM */
	public void setX(int x) {
		x = Util.clampi(x, 0,
				Display.getDisplayMode().getWidth() - this.getWidth());
		this.pos.setX(x);
	}

	/** @param y the new ordinate position of this frame in the WM */
	public void setY(int y) {
		y = Util.clampi(y, 0,
				Display.getDisplayMode().getHeight() - this.getHeight());
		this.pos.setY(y);
	}

	/**
	 * @param x the new abscissa position of this frame in the WM
	 * @param y the new ordinate position of this frame in the WM
	 */
	public void setPos(int x, int y) {
		x = Util.clampi(x, 0,
				Display.getDisplayMode().getWidth() - this.getWidth());
		y = Util.clampi(y, 0,
				Display.getDisplayMode().getHeight() - this.getHeight());
		this.pos.setPos(x, y);
	}

	/** @return the current width of this frame */
	public int getWidth() {
		return this.bounds.getWidth();
	}

	/**
	 * @return the width of this Frame before maximization/docking if currently
	 *         in such state, or the normal width
	 */
	public int getWindowedWidth() {
		if (this.isMaximized() || this.isDocked()) {
			return this.windowedBounds.getWidth();
		} else {
			return this.getWidth();
		}
	}

	/**
	 * @return the height of this Frame before maximization/docking if currently
	 *         in such state, or the normal height
	 */
	public int getWindowedHeight() {
		if (this.isMaximized() || this.isDocked()) {
			return this.windowedBounds.getHeight();
		} else {
			return this.getHeight();
		}
	}

	/** @return the current height of this frame */
	public int getHeight() {
		return this.bounds.getHeight();
	}

	/** @param w the new width of this frame */
	public void setWidth(int w) {
		if (!this.isResizable()) {
			return;
		}
		w = Util.clampi(w, this.minBounds.getWidth(), Display.getDisplayMode()
				.getWidth() - this.getX());
		this.bounds.setWidth(w);
		if (this.content != null) {
			this.content.setWidth(w - 2 * getBorderWidth());
		}
	}

	/** @param h the new height of this frame */
	public void setHeight(int h) {
		if (!this.isResizable()) {
			return;
		}
		h = Util.clampi(h, this.minBounds.getHeight(), Display.getDisplayMode()
				.getHeight() - this.getY());
		this.bounds.setHeight(h);
		if (this.content != null) {
			this.content.setHeight(h - 2 * getBorderWidth()
					- this.getTitleBarHeight());
		}
	}

	/**
	 * @param w the new width of this frame
	 * @param h the new height of this frame
	 */
	public void setBounds(int w, int h) {
		if (!this.isResizable()) {
			return;
		}
		w = Util.clampi(w, this.minBounds.getWidth(), Display.getDisplayMode()
				.getWidth() - this.getX());
		h = Util.clampi(h, this.minBounds.getHeight(), Display.getDisplayMode()
				.getHeight() - this.getY());
		this.bounds.setBounds(w, h);
		if (this.content != null) {
			this.content.setBounds(w - 2 * getBorderWidth(), h - 2
					* getBorderWidth() - this.getTitleBarHeight());
		}
	}

	/** @return the minimum width of this frame */
	public int getMinWidth() {
		return this.minBounds.getWidth();
	}

	/** @return the minimum height of this frame */
	public int getMinHeight() {
		return this.minBounds.getHeight();
	}

	/** @param title the new title of the frame as displayed in the titlebar */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return the title of the frame as displayed in the titlebar */
	public String getTitle() {
		return this.title;
	}

	/** @return true if this frame is currently focused in the WM */
	public boolean isFocused() {
		return this.focused;
	}

	/** @param focus the new focus state of this frame */
	public void setFocused(boolean focus) {
		if (focus != this.focused) {
			this.focusChangeTime = Sys.getTime();
		}
		this.focused = focus;
	}

	/**
	 * @return the last time the focus status changed
	 */
	public long getFocusChangeTime() {
		return this.focusChangeTime;
	}

	/** @return true if this frame is dragged in the WM */
	public boolean isDragged() {
		return this.dragged;
	}

	/** @param dragged the new drag state of this frame */
	public void setDragged(boolean dragged) {
		if (dragged != this.dragged) {
			this.dragChangeTime = Sys.getTime();
		}
		this.dragged = dragged;
	}

	/**
	 * @return the last time the drag status changed
	 */
	public long getDragChangeTime() {
		return this.dragChangeTime;
	}

	/** @return true if this frame is resized in the WM */
	public boolean isResized() {
		return this.resized;
	}

	/** @param dragged the new resize state of this frame */
	public void setResized(boolean resized) {
		if (resized != this.resized) {
			this.resizeChangeTime = Sys.getTime();
		}
		this.resized = resized;
	}

	/**
	 * @return the last time the resize status changed
	 */
	public long getResizeChangeTime() {
		return this.resizeChangeTime;
	}

	/** @param w the content of the frame */
	public void setContent(Widget w) {
		if (this.content != null) {
			this.content.setFrame(null);
		}
		this.content = w;
		this.content.setFrame(this);
		this.content.setBounds(
				this.getWidth() - 2 * getBorderWidth(),
				this.getHeight() - 2 * getBorderWidth()
						- this.getTitleBarHeight());
	}

	/** @return true if this frame is maximized */
	public boolean isMaximized() {
		return this.maximized;
	}

	/** Maximize / unmaximize this frame */
	public void toggleMaximize() {
		if (!this.hasTitlebar() || !this.isResizable() || this.isDocked()) {
			return;
		}

		this.maximized = !this.maximized;

		if (this.maximized) {
			this.windowedBounds = new Rectangle(this.bounds);
			this.windowedPos = new Point(this.pos);

			int w = Display.getDisplayMode().getWidth();
			int h = Display.getDisplayMode().getHeight();
			this.setPos(0, 0);
			this.setBounds(w, h);

		} else {
			this.setBounds(this.windowedBounds.getWidth(),
					this.windowedBounds.getHeight());
			this.setPos(this.windowedPos.getX(), this.windowedPos.getY());
		}
	}

	/**
	 * Toggle the docked / undocked state
	 */
	public void toggleDocked() {
		if (!this.isDocked() && this.isMaximized()) {
			toggleMaximize();
		}

		this.docked = !this.docked;

		if (this.docked) {
			this.setResizable(false);
			if (!this.isMaximized()) {
				this.windowedBounds = new Rectangle(this.bounds);
			}
		} else {
			this.setResizable(true);
			this.setBounds(this.windowedBounds.getWidth(),
					this.windowedBounds.getHeight());

			// do not do fadeout animation when moving out of dock
			this.apparitionTime = 0;
			this.removalTime = 0;
		}
	}

	/**
	 * @return true if this Frame is docked
	 */
	public boolean isDocked() {
		return this.docked;
	}

	/** @return true if this frame is resizable */
	public boolean isResizable() {
		return this.resizable;
	}

	/** @param r true to make this frame resizable */
	public void setResizable(boolean r) {
		this.resizable = r;
	}

	/** @return true if this frame can be moved */
	public boolean isMovable() {
		return true;
	}

	/** @return true if this frame has a titlebar */
	public boolean hasTitlebar() {
		return this.hasTitlebar;
	}

	/** @param t true for this frame to have a titlebar */
	public void setTitlebar(boolean t) {
		if (this.isMaximized() || this.isDocked()) {
			return;
		}

		this.hasTitlebar = t;
		if (this.content != null) {
			this.content.setBounds(
					this.getWidth() - 2 * getBorderWidth(),
					this.getHeight() - 2 * getBorderWidth()
							- this.getTitleBarHeight());
		}
	}

	/** Resize this frame to fit the preferred size of its content */
	public void pack() {
		if (this.content != null) {
			int w = this.content.getPreferredWidth(this.getWidth() - 2
					* getBorderWidth());
			int h = this.content.getPreferredHeight(this.getHeight() - 2
					* getBorderWidth() - this.getTitleBarHeight());
			this.setBounds(w + getBorderWidth() * 2, h + 2 * getBorderWidth()
					+ this.getTitleBarHeight());
		} else {
			this.setBounds(20, 20);
		}
	}

	@Override
	public void mouseHover(int x, int y) {
		int border = getBorderWidth();
		boolean inside = border < x && x < this.getWidth() - border
				&& border + getTitleBarHeight() < y
				&& y < this.getHeight() - border;
		if (inside) {
			if (this.content != null) {
				this.content.mouseHover(x - border, y - border
						- getTitleBarHeight());
				this.contentHover = true;
			}
		} else if (this.contentHover && this.content != null) {
			this.content.mouseOut();
		}
	}

	@Override
	public void mouseOut() {
		if (contentHover && this.content != null) {
			this.content.mouseOut();
			this.contentHover = false;
		}
	}

	@Override
	public void mouseDown(int button) {
		if (contentHover && this.content != null) {
			if (button == 0) {
				this.contentLeftMouseDown = true;
			} else if (button == 1) {
				this.contentRightMouseDown = true;
			} else if (button == 2) {
				this.contentCenterMouseDown = true;
			}
			this.content.mouseDown(button);
		}
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		int border = getBorderWidth();
		boolean hadOne = false;
		if (this.contentLeftMouseDown && button == 0) {
			hadOne = true;
			this.contentLeftMouseDown = false;
		} else if (this.contentRightMouseDown && button == 1) {
			hadOne = true;
			this.contentRightMouseDown = false;
		} else if (this.contentCenterMouseDown && button == 2) {
			hadOne = true;
			this.contentCenterMouseDown = false;
		}

		if (hadOne && this.content != null) {
			this.content.mouseUp(button, x - border, y - border
					- getTitleBarHeight());
		}
	}

	public void setControlCloseHover(boolean h) {
		if (h != this.controlCloseHover) {
			this.controlCloseHoverTime = Sys.getTime();
		}
		this.controlCloseHover = h;
	}

	public void setControlCloseDown(boolean d) {
		this.controlCloseDown = d;
	}

	public void setControlMaximizeHover(boolean h) {
		if (h != this.controlMaximizeHover) {
			this.controlMaximizeHoverTime = Sys.getTime();
		}
		this.controlMaximizeHover = h;
	}

	public void setControlMaximizeDown(boolean d) {
		this.controlMaximizeDown = d;
	}

	/** @return the content of the frame */
	public Widget getContent() {
		return this.content;
	}

	/**
	 * Tests whether a specific 2D coordinate is contained in the bounds of this
	 * frame
	 * 
	 * @param x abscissa in 2D space
	 * @param y ordinate in 2D space
	 * @return true if the provided point is contained in this frame
	 */
	public boolean contains(int x, int y) {
		return bounds.contains(x - pos.getX(), y - pos.getY());
	}

	/**
	 * Determines what {@link Region} of the Frame corresponds to a particular
	 * point in 2D space
	 * 
	 * @param mx abscissa in 2D space
	 * @param my ordinate in 2D space
	 * @return the Region of the frame corresponding the provided coordinates
	 */
	public Region getRegion(int mx, int my) {
		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeight();

		int px = 0;
		int py = 0;

		int borderWidth = getBorderWidth();
		int titlebarHeight = getTitleBarHeight();

		boolean isTitle = false;

		if (mx < x) {
			return Region.OUT;
		} else if (mx <= x + borderWidth) {
			px = 1; // left border
		} else if (mx <= x + cornerWidth) {
			px = 2; // center with left corner tolerance
		} else if (mx <= x + w - cornerWidth) {
			px = 3; // center
		} else if (mx <= x + w - borderWidth) {
			px = 4; // center with right corner tolerance
		} else if (mx <= x + w) {
			px = 5; // right border
		} else {
			return Region.OUT;
		}

		if (my < y) {
			return Region.OUT;
		} else if (my <= y + borderWidth) {
			py = 1; // top border
		} else if (my <= y + cornerWidth) {
			py = 2; // titlebar with top corner tolerance
		} else if (my <= y + borderWidth + titlebarHeight) {
			py = 3; // titlebar
		} else if (my <= y + h - cornerWidth) {
			py = 4; // center
		} else if (my <= y + h - borderWidth) {
			py = 5; // center with bottom corner tolerance
		} else if (my <= y + h) {
			py = 6; // bottom border
		} else {
			return Region.OUT;
		}

		switch (px) {
		case 1:
			switch (py) {
			case 1:
			case 2:
				return Region.TOP_LEFT;
			case 3:
			case 4:
				return Region.LEFT;
			case 5:
			case 6:
				return Region.BOT_LEFT;
			}
			break;
		case 2:
			switch (py) {
			case 1:
				return Region.TOP_LEFT;
			case 2:
			case 3:
				isTitle = true;
				break;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT_LEFT;
			}
			break;
		case 3:
			switch (py) {
			case 1:
				return Region.TOP;
			case 2:
			case 3:
				isTitle = true;
				break;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT;
			}
			break;
		case 4:
			switch (py) {
			case 1:
				return Region.TOP_RIGHT;
			case 2:
			case 3:
				isTitle = true;
				break;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT_RIGHT;
			}
			break;
		case 5:
			switch (py) {
			case 1:
			case 2:
				return Region.TOP_RIGHT;
			case 3:
			case 4:
				return Region.RIGHT;
			case 5:
			case 6:
				return Region.BOT_RIGHT;
			}
		}

		if (isTitle && !this.hasTitlebar()) {
			return Region.CONTENT;
		} else if (isTitle) {
			int controlWidth = Theme.get().frameControlsWidth;
			int controlBorder = Theme.get().frameControlsBorder;
			int spaceLeft = w - 2 * borderWidth;
			int acc = 0;
			int controlsNum = 2;

			List<Controls> controls = Theme.get().frameControlsOrder;
			for (Controls c : controls) {
				if (c.equals(Controls.TITLE)) {
					int titleWidth = spaceLeft - controlsNum
							* (controlWidth + controlBorder);

					if (x + borderWidth + acc < mx
							&& mx < x + borderWidth + acc + titleWidth) {
						return Region.TITLE;
					}

					acc += titleWidth + controlBorder;
				} else {
					if (x + borderWidth + acc < mx
							&& mx < x + borderWidth + acc + controlWidth) {
						switch (c) {
						case CLOSE:
							return Region.CLOSE;
						case MAXIMIZE:
							if (this.isResizable() && !this.isDocked())
								return Region.MAXIMIZE;
							else
								return Region.TITLE;
						}
					}

					acc += controlWidth + controlBorder;
					spaceLeft -= controlWidth + controlBorder;
					controlsNum--;
				}
			}

			return Region.TITLE;
		} else {
			return Region.OUT;
		}
	}

	/**
	 * @return the time at which this Frame appeared in the WM
	 */
	public long getApparitionTime() {
		return this.apparitionTime;
	}

	/**
	 * @return the time at which this Frame was removed from the WM
	 */
	public long getRemovalTime() {
		return this.removalTime;
	}

	/**
	 * @return the Widget that currently holds the Keyboard Focus in this Frame
	 */
	public Focusable getKeyboardFocus() {
		return this.keyboardFocus;
	}

	/**
	 * Request keyboard focus for a Widget in this Frame
	 * 
	 * @param a the new Widget holding keyboard focus in this Frame, can be null
	 */
	public void setKeyboardFocus(Focusable a) {
		if (this.keyboardFocus != null)
			this.keyboardFocus.setFocus(false);
		this.keyboardFocus = a;
		if (a != null)
			a.setFocus(true);
	}

	/** @return pixel height of the titlebar */
	protected abstract int getTitleBarHeight();

	/** @return pixel width of the frame border */
	protected abstract int getBorderWidth();

	/** @return Color of the border when focused */
	protected abstract Color getBorderFocusedColor();

	/** @return Color of the border when not focused */
	protected abstract Color getBorderUnfocusedColor();

	/** @return Color of the outer border when focused */
	protected abstract Color getBorderOuterFocusedColor();

	/** @return Color of the outer border when not focused */
	protected abstract Color getBorderOuterUnfocusedColor();

	/** @return the color of the focused titlebar */
	protected abstract Color getFrameTitlebarFocusedColor();

	/** @return the color of the unfocused titlebar */
	protected abstract Color getFrameTitlebarUnfocusedColor();

	protected abstract Color getFrameFocusedColor();

	protected abstract Color getFrameUnfocusedColor();

	protected abstract Color getFrameShadowFocusedColor();

	protected abstract Color getFrameShadowUnfocusedColor();

	protected abstract float getFrameMovedAlpha();

	protected abstract float getFrameResizedAlpha();

	protected abstract float getFrameFocusedAlpha();

	protected abstract float getFrameUnfocusedAlpha();

	/**
	 * @return the current frame UI color, acounting for animations
	 */
	public Color getUiColor() {
		Color focusCol = getFrameFocusedColor();
		Color unfocusCol = getFrameUnfocusedColor();

		if (Config.get().isWmAnimations()) {
			long t = Sys.getTime();
			float animLen = (float) Config.get().getWmAnimationsLength();

			float focusAnim = Util.clampf(
					(float) (t - this.getFocusChangeTime()) / animLen, 0.0f,
					1.0f);
			if (!this.isFocused()) {
				focusAnim = 1.0f - focusAnim;
			}
			return focusCol.blend(unfocusCol, focusAnim);
		} else {
			if (this.isFocused()) {
				return focusCol;
			} else {
				return unfocusCol;
			}
		}
	}

	/**
	 * @return the current frame shadow color, accounting for animations
	 */
	public Color getShadowColor() {
		Color shadowCol = getFrameShadowFocusedColor();
		Color shadowUnfCol = getFrameShadowUnfocusedColor();

		if (Config.get().isWmAnimations()) {
			long t = Sys.getTime();
			float animLen = (float) Config.get().getWmAnimationsLength();

			float focusAnim = Util.clampf(
					(float) (t - this.getFocusChangeTime()) / animLen, 0.0f,
					1.0f);
			if (!this.isFocused()) {
				focusAnim = 1.0f - focusAnim;
			}
			return shadowCol.blend(shadowUnfCol, focusAnim);
		} else {
			if (this.isFocused()) {
				return shadowCol;
			} else {
				return shadowUnfCol;
			}
		}
	}

	/**
	 * Primary alpha : applies to every element of the Frame without exception
	 * 
	 * @return the current frame alpha modifier for focus, resizing and moving,
	 *         accounting animations
	 */
	public float getModAlpha() {
		float movedAlpha = getFrameMovedAlpha();
		float resizedAlpha = getFrameResizedAlpha();

		if (Config.get().isWmAnimations()) {

			long t = Sys.getTime();
			float animLen = (float) Config.get().getWmAnimationsLength();

			float movedAnim = Util.clampf(
					(float) (t - this.getDragChangeTime()) / animLen, 0.0f,
					1.0f);
			if (!this.isDragged()) {
				movedAnim = 1.0f - movedAnim;
			}
			float resizeAnim = Util.clampf(
					(float) (t - this.getResizeChangeTime()) / animLen, 0.0f,
					1.0f);
			if (!this.isResized()) {
				resizeAnim = 1.0f - resizeAnim;
			}
			float appearAnim = Util.clampf((float) (t - this.apparitionTime)
					/ animLen, 0.0f, 1.0f);
			if (this.isDocked()) {
				appearAnim = 1.0f;
			}
			float removeAnim = 1.0f - Util.clampf(
					(float) (t - this.removalTime) / animLen, 0.0f, 1.0f);
			if (this.isDocked()) {
				removeAnim = 1.0f;
			}
			if (removeAnim == 0.0f && this.isActive()) {
				removeAnim = 1.0f;
			}

			float alpha = (movedAlpha * movedAnim + 1.0f * (1.0f - movedAnim));
			alpha *= (resizedAlpha * resizeAnim + 1.0f * (1.0f - resizeAnim));
			alpha *= appearAnim * removeAnim;

			return alpha;
		} else {
			float ra = 1.0f;
			if (this.isResized()) {
				ra *= resizedAlpha;
			} else if (this.isDragged()) {
				ra *= movedAlpha;
			}
			if (!this.isActive()) {
				ra = 0.0f;
			}
			return ra;
		}
	}

	/**
	 * Secondary alpha : applies to the background elements of the Frame, ie not
	 * text or content.
	 * 
	 * @return the current frame UI alpha, accounting for animations
	 */
	public float getUiAlpha() {
		float focusAlpha = getFrameFocusedAlpha();
		float unfocusAlpha = getFrameUnfocusedAlpha();

		if (Config.get().isWmAnimations()) {

			long t = Sys.getTime();
			float animLen = (float) Config.get().getWmAnimationsLength();

			float focusAnim = Util.clampf(
					(float) (t - this.getFocusChangeTime()) / animLen, 0.0f,
					1.0f);
			if (!this.isFocused()) {
				focusAnim = 1.0f - focusAnim;
			}

			float alpha = focusAlpha * focusAnim + unfocusAlpha
					* (1.0f - focusAnim);

			return alpha;
		} else {
			if (this.isFocused()) {
				return focusAlpha;
			} else {
				return unfocusAlpha;
			}
		}
	}

	/**
	 * When focused/unfocused, if animations are enabled, the frame
	 * progressively moves graphically from one state to another
	 * 
	 * @return 0.0 if the frame is focused, 1.0 if it is not focused, or a value
	 *         in between
	 */
	public float getFocusAnim() {
		long t = Sys.getTime();
		float animLen = (float) Config.get().getWmAnimationsLength();
		float focusAnim = Util.clampf((float) (t - this.getFocusChangeTime())
				/ animLen, 0.0f, 1.0f);
		if (!this.isFocused()) {
			focusAnim = 1.0f - focusAnim;
		}
		if (!Config.get().isWmAnimations()) {
			focusAnim = ((this.isFocused()) ? 1.0f : 0.0f);
		}
		return focusAnim;
	}

}
