package bifstk.wm;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;
import bifstk.wm.geom.Point;
import bifstk.wm.geom.Rectangle;
import bifstk.wm.geom.Region;
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

	/** true if the Maximize Control button is hovered by the mouse */
	private boolean controlMaximizeHover = false;
	/** true if the Maximize Control button is clicked by the mouse */
	private boolean controlMaximizeDown = false;

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
		this.minBounds = new Rectangle(60, 60);
		this.pos = new Point(x, y);
	}

	/** {@inheritDoc} */
	@Override
	public void render(float alpha, Color uiColor, float uiAlpha) {
		int x, y, w, h;
		float alpha2 = alpha * uiAlpha;
		int borderWidth = getBorderWidth();
		int titlebarHeight = getTitleBarHeight();

		x = this.getX();
		y = this.getY();
		w = this.getWidth();
		h = this.getHeight();

		Color borderCol = null;
		Color borderBorderCol = null;
		if (this.isFocused()) {
			borderCol = getBorderFocusedColor();
			borderBorderCol = getBorderOuterFocusedColor();
		} else {
			borderCol = getBorderUnfocusedColor();
			borderBorderCol = getBorderOuterUnfocusedColor();
		}
		borderCol.use(alpha2);
		GL11.glBegin(GL11.GL_QUADS);
		// top border
		GL11.glVertex2i(x + borderWidth, y);
		GL11.glVertex2i(x + w - borderWidth, y);
		GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + borderWidth);
		// left border
		GL11.glVertex2i(x, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
		GL11.glVertex2i(x, y + h - borderWidth);
		// right border
		GL11.glVertex2i(x + w, y + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
		GL11.glVertex2i(x + w, y + h - borderWidth);
		// bottom border
		GL11.glVertex2i(x + borderWidth, y + h);
		GL11.glVertex2i(x + w - borderWidth, y + h);
		GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
		GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
		GL11.glEnd();

		int precision = 5;
		if (isFrameBorderRounded()) {
			Util.drawFilledArc(x + borderWidth, y + borderWidth, borderWidth,
					(float) Math.PI, (float) Math.PI / 2.0f, precision,
					borderCol, alpha2, borderCol, alpha2);

			Util.drawFilledArc(x + w - borderWidth, y + borderWidth,
					borderWidth, (float) -Math.PI / 2.0f,
					(float) Math.PI / 2.0f, precision, borderCol, alpha2,
					borderCol, alpha2);

			Util.drawFilledArc(x + w - borderWidth, y + h - borderWidth,
					borderWidth, 0.0f, (float) Math.PI / 2.0f, precision,
					borderCol, alpha2, borderCol, alpha2);

			Util.drawFilledArc(x + borderWidth, y + h - borderWidth,
					borderWidth, (float) Math.PI / 2.0f,
					(float) Math.PI / 2.0f, precision, borderCol, alpha2,
					borderCol, alpha2);

			if (borderWidth > 1) {
				// metaborder
				Util.drawLineArc(x + borderWidth, y + borderWidth, borderWidth,
						(float) Math.PI, (float) Math.PI / 2.0f, precision,
						borderBorderCol, alpha2);

				Util.drawLineArc(x + w - borderWidth, y + borderWidth,
						borderWidth, (float) -Math.PI / 2.0f,
						(float) Math.PI / 2.0f, precision, borderBorderCol,
						alpha2);

				Util.drawLineArc(x + w - borderWidth, y + h - borderWidth,
						borderWidth, 0.0f, (float) Math.PI / 2.0f, precision,
						borderBorderCol, alpha2);

				Util.drawLineArc(x + borderWidth, y + h - borderWidth,
						borderWidth, (float) Math.PI / 2.0f,
						(float) Math.PI / 2.0f, precision, borderBorderCol,
						alpha2);

				GL11.glBegin(GL11.GL_LINES);
				borderBorderCol.use(alpha2);

				GL11.glVertex2i(x + borderWidth, y + 1);
				GL11.glVertex2i(x + w - borderWidth, y + 1);

				GL11.glVertex2i(x + w, y + borderWidth);
				GL11.glVertex2i(x + w, y + h - borderWidth);

				GL11.glVertex2i(x + borderWidth, y + h);
				GL11.glVertex2i(x + w - borderWidth, y + h);

				GL11.glVertex2i(x + 1, y + borderWidth);
				GL11.glVertex2i(x + 1, y + h - borderWidth);

				GL11.glEnd();
			}
		} else {
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + borderWidth, y);
			GL11.glVertex2i(x + borderWidth, y + borderWidth);
			GL11.glVertex2i(x, y + borderWidth);

			GL11.glVertex2i(x + w, y);
			GL11.glVertex2i(x + w - borderWidth, y);
			GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
			GL11.glVertex2i(x + w, y + borderWidth);

			GL11.glVertex2i(x, y + h);
			GL11.glVertex2i(x + borderWidth, y + h);
			GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
			GL11.glVertex2i(x, y + h - borderWidth);

			GL11.glVertex2i(x + w, y + h);
			GL11.glVertex2i(x + w - borderWidth, y + h);
			GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
			GL11.glVertex2i(x + w, y + h - borderWidth);
			GL11.glEnd();

			if (borderWidth > 1) {
				// metaborder
				borderBorderCol.use(alpha2);
				GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex2i(x + 2, y + 2);
				GL11.glVertex2i(x + w - 1, y + 2);
				GL11.glVertex2i(x + w - 1, y + h - 1);
				GL11.glVertex2i(x + 2, y + h - 1);
				GL11.glEnd();
			}
		}

		if (this.isFocused()) {
			getFrameTitlebarFocusedColor().use(alpha2);
		} else {
			getFrameTitlebarUnfocusedColor().use(alpha2);
		}

		// title-bar
		if (this.hasTitlebar) {
			// background
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x + borderWidth, y + titlebarHeight + borderWidth);
			GL11.glVertex2i(x + w - borderWidth, y + titlebarHeight
					+ borderWidth);
			GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
			GL11.glVertex2i(x + borderWidth, y + borderWidth);
			GL11.glEnd();

			int controlWidth = Theme.getFrameControlsWidth();
			int controlHeight = Theme.getFrameControlsHeight();
			int controlBorder = Theme.getFrameControlsBorder();
			int spaceLeft = w - 2 * borderWidth;
			int acc = 0;
			int controlsNum = 2;

			if (!this.isResizable()) {
				controlsNum = 1;
			}

			// title & controls
			List<Controls> controls = Theme.getFrameControlsOrder();
			for (Controls c : controls) {
				if (c.equals(Controls.TITLE)) {
					int titleWidth = spaceLeft - controlsNum
							* (controlWidth + controlBorder);
					GL11.glEnable(GL11.GL_SCISSOR_TEST);
					Util.pushScissor(x + borderWidth + acc, Display
							.getDisplayMode().getHeight()
							- y
							- titlebarHeight
							- borderWidth, titleWidth, titlebarHeight);
					Fonts.getNormal().drawString(x + borderWidth + acc,
							y + borderWidth, this.title, Color.WHITE, alpha);
					Util.popScissor();
					GL11.glDisable(GL11.GL_SCISSOR_TEST);

					acc += titleWidth + controlBorder;
				} else {
					Image img = null;
					Color col = null;
					int yClickDec = 0;

					switch (c) {
					case CLOSE:
						img = Theme.getFrameControlCloseImage();
						if (this.controlCloseDown && this.controlCloseHover) {
							col = Theme.getFrameControlsCloseClickColor();
							yClickDec = 1;
						} else if (this.controlCloseHover) {
							col = Theme.getFrameControlsCloseHoverColor();
						} else {
							col = Theme.getFrameControlsCloseColor();
						}
						break;
					case MAXIMIZE:
						if (this.isResizable()) {
							img = Theme.getFrameControlMaximizeImage();
							if (this.controlMaximizeDown
									&& this.controlMaximizeHover) {
								col = Theme
										.getFrameControlsMaximizeClickColor();
								yClickDec = 1;
							} else if (this.controlMaximizeHover) {
								col = Theme
										.getFrameControlsMaximizeHoverColor();
							} else {
								col = Theme.getFrameControlsMaximizeColor();
							}
						}
						break;
					}
					if (img != null) {
						Texture tex = img.getTexture();

						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						Util.pushScissor(x + borderWidth + acc, Display
								.getDisplayMode().getHeight()
								- y
								- controlHeight - borderWidth, controlWidth,
								controlHeight);

						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glBindTexture(GL11.GL_TEXTURE_2D,
								tex.getTextureID());
						col.use(alpha);
						GL11.glBegin(GL11.GL_QUADS);
						GL11.glTexCoord2f(0.0f, 0.0f);
						GL11.glVertex2i(x + borderWidth + acc, y + yClickDec
								+ borderWidth);
						GL11.glTexCoord2f(0.0f, 1.0f);
						GL11.glVertex2i(x + borderWidth + acc, y + yClickDec
								+ borderWidth + tex.getTextureHeight());
						GL11.glTexCoord2f(1.0f, 1.0f);
						GL11.glVertex2i(
								x + borderWidth + acc + tex.getTextureWidth(),
								y + yClickDec + borderWidth
										+ tex.getTextureHeight());
						GL11.glTexCoord2f(1.0f, 0.0f);
						GL11.glVertex2i(
								x + borderWidth + acc + tex.getTextureWidth(),
								y + yClickDec + borderWidth);
						GL11.glEnd();

						GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

						Util.popScissor();

						acc += controlWidth + controlBorder;
						spaceLeft -= controlWidth + controlBorder;
						controlsNum--;
					}
				}
			}
		}

		// content
		if (this.content == null) {
			uiColor.use(alpha2);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x + borderWidth, y + titlebarHeight + borderWidth);
			GL11.glVertex2i(x + w - borderWidth, y + titlebarHeight
					+ borderWidth);
			GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
			GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
			GL11.glEnd();
		} else {
			GL11.glPushMatrix();
			GL11.glTranslatef(x + borderWidth,
					y + titlebarHeight + borderWidth, 0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			Util.pushScissor(x + borderWidth, Display.getDisplayMode()
					.getHeight() - (y + h) + borderWidth, w - 2 * borderWidth,
					h - 2 * borderWidth - titlebarHeight);
			this.content.render(alpha, uiColor, uiAlpha);
			Util.popScissor();
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glPopMatrix();
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
		this.focused = focus;
	}

	/** @return true if this frame is dragged in the WM */
	public boolean isDragged() {
		return this.dragged;
	}

	/** @param dragged the new drag state of this frame */
	public void setDragged(boolean dragged) {
		this.dragged = dragged;
	}

	/** @return true if this frame is resized in the WM */
	public boolean isResized() {
		return this.resized;
	}

	/** @param dragged the new resize state of this frame */
	public void setResized(boolean resized) {
		this.resized = resized;
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
		this.docked = !this.docked;

		if (this.docked) {
			this.setResizable(false);
			this.windowedBounds = new Rectangle(this.bounds);
		} else {
			this.setResizable(true);
			this.setBounds(this.windowedBounds.getWidth(),
					this.windowedBounds.getHeight());
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
			int w = this.content.getPreferredWidth();
			int h = this.content.getPreferredHeight();
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
		this.controlCloseHover = h;
	}

	public void setControlCloseDown(boolean d) {
		this.controlCloseDown = d;
	}

	public void setControlMaximizeHover(boolean h) {
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
			int controlWidth = Theme.getFrameControlsWidth();
			int controlBorder = Theme.getFrameControlsBorder();
			int spaceLeft = w - 2 * borderWidth;
			int acc = 0;
			int controlsNum = 2;

			List<Controls> controls = Theme.getFrameControlsOrder();
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

	/** @return true if the border corner should be rounded */
	protected abstract boolean isFrameBorderRounded();

	/** @return the color of the focused titlebar */
	protected abstract Color getFrameTitlebarFocusedColor();

	/** @return the color of the unfocused titlebar */
	protected abstract Color getFrameTitlebarUnfocusedColor();

}
