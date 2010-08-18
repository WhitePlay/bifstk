package bifstk.wm;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.TrueTypeFont;
import bifstk.gl.Util;
import bifstk.wm.geom.Point;
import bifstk.wm.geom.Rectangle;
import bifstk.wm.geom.Region;
import bifstk.wm.ui.Widget;

/**
 * a Frame is a Window in the Window Manager
 * <p>
 * It has a title bar and contains widgets
 * 
 */
public class Frame implements Drawable {

	/** dimensions */
	private Rectangle bounds = null;

	/** position in the WM */
	private Point pos = null;

	/** minimum dimensions */
	private Rectangle minBounds = null;

	/** true if the frame is currently focused in the WM */
	private boolean focused = false;

	/** true if the frame is currently dragged in the WM */
	private boolean dragged = false;

	/** true if the frame is currently resized in the WM */
	private boolean resized = false;

	/** width of the corner in pixels for mouse corner resize */
	private final int cornerWidth = 15;

	/** height of the titlebar in pixels */
	private final int titlebarHeight = 20;

	/** content of the frame */
	private Widget content = null;

	/**
	 * Default constructor
	 * 
	 * @param x abscissa in the WM
	 * @param y ordinate in the WM
	 */
	public Frame(int x, int y) {
		this.bounds = new Rectangle(100, 100);
		this.minBounds = new Rectangle(60, 60);
		this.pos = new Point(x, y);
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
		this.pos = new Point(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(float alpha) {
		int x, y, w, h;

		x = this.getX();
		y = this.getY();
		w = this.getWidth();
		h = this.getHeight();

		int borderWidth = Theme.getFrameBorderWidth();
		int titlebarHeight = getTitleBarHeight();

		if (this.isDragged()) {
			alpha *= Theme.getFrameMovedAlpha();
		} else if (this.isResized()) {
			alpha *= Theme.getFrameResizedAlpha();
		}

		if (!this.isFocused()) {
			alpha *= Theme.getFrameUnfocusedAlpha();
		}

		if (Theme.isFrameShadowEnabled()) {
			Util.drawDroppedShadow(x, y, w, h, Theme.getFrameShadowRadius(),
					Theme.getFrameShadowAlpha() * alpha);
		}

		if (this.isFocused()) {
			Theme.getFrameBorderFocusedColor().use(alpha);
		} else {
			Theme.getFrameBorderUnfocusedColor().use(alpha);
		}

		GL11.glBegin(GL11.GL_QUADS);
		// top border
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x + w, y);
		GL11.glVertex2i(x + w, y + borderWidth);
		GL11.glVertex2i(x, y + borderWidth);
		// left border
		GL11.glVertex2i(x, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + h);
		GL11.glVertex2i(x, y + h);
		// right border
		GL11.glVertex2i(x + w, y + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + h);
		GL11.glVertex2i(x + w, y + h);
		// bottom border
		GL11.glVertex2i(x + borderWidth, y + h);
		GL11.glVertex2i(x + w - borderWidth, y + h);
		GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
		GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
		GL11.glEnd();

		if (this.isFocused()) {
			Theme.getFrameTitlebarFocusedColor().use(alpha);
		} else {
			Theme.getFrameTitlebarUnfocusedColor().use(alpha);
		}

		// title-bar
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x + borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + borderWidth);
		GL11.glEnd();

		// content
		if (this.content == null) {
			GL11.glColor4f(0.9f, 0.9f, 0.9f, alpha);
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
			GL11.glScissor(x + borderWidth, Display.getDisplayMode()
					.getHeight() - (y + h) + borderWidth, w - 2 * borderWidth,
					h - 2 * borderWidth - titlebarHeight);

			this.content.render(alpha);

			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glPopMatrix();
		}

		// draw little info area at the center of the window to display
		// the current position/size while dragging/resizing
		if (this.dragged || this.resized) {
			TrueTypeFont font = Fonts.getSmall();
			String msg = "";
			if (this.resized) {
				msg = w + "x" + h;
			} else {
				msg = x + ":" + y;
			}

			int msgW = font.getWidth(msg);
			int msgH = font.getHeight(msg);
			int mx = x + (w / 2) - (msgW / 2);
			int my = y + (h / 2) - (msgH / 2);

			GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(mx, my);
			GL11.glVertex2i(mx, my + msgH);
			GL11.glVertex2i(mx + msgW, my + msgH);
			GL11.glVertex2i(mx + msgW, my);
			GL11.glEnd();

			if (Theme.isFrameShadowEnabled()) {
				Util.drawDroppedShadow(mx, my, msgW, msgH,
						Theme.getFrameShadowRadius(),
						Theme.getFrameShadowAlpha());
			}

			font.drawString(mx, my, msg, Color.BLACK, 1.0f);
		}

	}

	/**
	 * @return the abscissa of the top left corner of this frame
	 */
	public int getX() {
		return this.pos.getX();
	}

	/**
	 * @return the ordinate of the top left corner of this frame
	 */
	public int getY() {
		return this.pos.getY();
	}

	/**
	 * @param x the new abscissa position of this frame in the WM
	 */
	public void setX(int x) {
		this.pos.setX(x);
	}

	/**
	 * @param y the new ordinate position of this frame in the WM
	 */
	public void setY(int y) {
		this.pos.setY(y);
	}

	/**
	 * @param x the new abscissa position of this frame in the WM
	 * @param y the new ordinate position of this frame in the WM
	 */
	public void setPos(int x, int y) {
		this.pos.setPos(x, y);
	}

	/**
	 * @return the current width of this frame
	 */
	public int getWidth() {
		return this.bounds.getWidth();
	}

	/**
	 * @return the current height of this frame
	 */
	public int getHeight() {
		return this.bounds.getHeight();
	}

	/**
	 * @param w the new width of this frame
	 */
	public void setWidth(int w) {
		w = Math.max(w, this.minBounds.getWidth());
		this.bounds.setWidth(w);
		if (this.content != null) {
			this.content.setWidth(w - 2 * Theme.getFrameBorderWidth());
		}
	}

	/**
	 * @param h the new height of this frame
	 */
	public void setHeight(int h) {
		h = Math.max(h, this.minBounds.getHeight());
		this.bounds.setHeight(h);
		if (this.content != null) {
			this.content.setHeight(h - 2 * Theme.getFrameBorderWidth()
					- this.getTitleBarHeight());
		}
	}

	/**
	 * @param w the new width of this frame
	 * @param h the new height of this frame
	 */
	public void setBounds(int w, int h) {
		w = Math.max(w, this.minBounds.getWidth());
		h = Math.max(h, this.minBounds.getHeight());
		this.bounds.setBounds(w, h);
		if (this.content != null) {
			this.content.setBounds(w - 2 * Theme.getFrameBorderWidth(), h - 2
					* Theme.getFrameBorderWidth() - this.getTitleBarHeight());
		}
	}

	/**
	 * @return the minimum width of this frame
	 */
	public int getMinWidth() {
		return this.minBounds.getWidth();
	}

	/**
	 * @return the minimum height of this frame
	 */
	public int getMinHeight() {
		return this.minBounds.getHeight();
	}

	/**
	 * @param x the minimum width of this frame
	 */
	public void setMinWidth(int x) {
		this.minBounds.setWidth(x);
	}

	/**
	 * @param y the minimum width of this frame
	 */
	public void setMinHeight(int y) {
		this.minBounds.setHeight(y);
	}

	/**
	 * @param w the new minimum width of this frame
	 * @param h the new minimum height of this frame
	 */
	public void setMinBounds(int w, int h) {
		this.minBounds.setBounds(w, h);
	}

	/**
	 * @return true if this frame is currently focused in the WM
	 */
	public boolean isFocused() {
		return this.focused;
	}

	/**
	 * @param focus the new focus state of this frame
	 */
	public void setFocused(boolean focus) {
		this.focused = focus;
	}

	/**
	 * @return true if this frame is dragged in the WM
	 */
	public boolean isDragged() {
		return this.dragged;
	}

	/**
	 * @param dragged the new drag state of this frame
	 */
	public void setDragged(boolean dragged) {
		this.dragged = dragged;
	}

	/**
	 * @return true if this frame is resized in the WM
	 */
	public boolean isResized() {
		return this.resized;
	}

	/**
	 * @param dragged the new resize state of this frame
	 */
	public void setResized(boolean resized) {
		this.resized = resized;
	}

	/**
	 * @param w the content of the frame
	 */
	public void setContent(Widget w) {
		this.content = w;
		this.content.setBounds(
				this.getWidth() - 2 * Theme.getFrameBorderWidth(),
				this.getHeight() - 2 * Theme.getFrameBorderWidth()
						- this.getTitleBarHeight());
	}

	/**
	 * Resize this frame to fit the preferred size
	 * of its content
	 */
	public void pack() {
		if (this.content != null) {
			int w = this.content.getPreferredWidth();
			int h = this.content.getPreferredHeight();
			this.setBounds(w + Theme.getFrameBorderWidth() * 2,
					h + Theme.getFrameBorderWidth() + this.getTitleBarHeight());
		} else {
			this.setBounds(20, 20);
		}
	}

	/**
	 * @return the content of the frame
	 */
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

		int px = 0;
		int py = 0;

		int borderWidth = Theme.getFrameBorderWidth();
		int titlebarHeight = getTitleBarHeight();

		if (mx < x) {
			return Region.OUT;
		} else if (mx <= x + borderWidth) {
			px = 1; // left border
		} else if (mx <= x + cornerWidth) {
			px = 2; // center with left corner tolerance
		} else if (mx <= x + this.getWidth() - cornerWidth) {
			px = 3; // center
		} else if (mx <= x + this.getWidth() - borderWidth) {
			px = 4; // center with right corner tolerance
		} else if (mx <= x + this.getWidth()) {
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
		} else if (my <= y + this.getHeight() - cornerWidth) {
			py = 4; // center
		} else if (my <= y + this.getHeight() - borderWidth) {
			py = 5; // center with bottom corner tolerance
		} else if (my <= y + this.getHeight()) {
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
		case 2:
			switch (py) {
			case 1:
				return Region.TOP_LEFT;
			case 2:
			case 3:
				return Region.TITLE;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT_LEFT;
			}
		case 3:
			switch (py) {
			case 1:
				return Region.TOP;
			case 2:
			case 3:
				return Region.TITLE;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT;
			}
		case 4:
			switch (py) {
			case 1:
				return Region.TOP_RIGHT;
			case 2:
			case 3:
				return Region.TITLE;
			case 4:
			case 5:
				return Region.CONTENT;
			case 6:
				return Region.BOT_RIGHT;
			}
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

		return Region.OUT;
	}

	/**
	 * @return pixel height of the titlebar
	 */
	private int getTitleBarHeight() {
		// return Fonts.getNormal().getHeight() + 2;
		return titlebarHeight;
	}

	@Override
	public String toString() {
		return bounds.toString() + pos.toString();
	}

}
