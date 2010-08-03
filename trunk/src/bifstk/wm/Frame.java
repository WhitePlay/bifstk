package bifstk.wm;

import org.lwjgl.opengl.GL11;

import bifstk.wm.geom.Point;
import bifstk.wm.geom.Rectangle;

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

	/** true if the frame is currently focused in the WM */
	private boolean focused = false;

	/** true if the frame is currently dragged in the WM */
	private boolean dragged = false;

	/** true if the frame is currently resized in the WM */
	private boolean resized = false;

	/** width in pixels of the resize border outside the frame */
	private int borderWidth = 5;
	/** height in pixels of the titlebar */
	private int titlebarHeight = 20;

	/**
	 * Regions that compose the frame: borders, title and content
	 * 
	 * <pre>
	 *  1_________2_________3
	 *  |_________4_________|
	 *  |                   |
	 *  5         6         7    11
	 *  |                   |
	 *  8_________9________10
	 * </pre>
	 * <ul>
	 * <li>1: top left border
	 * <li>2: top border
	 * <li>3: top right border
	 * <li>4: title
	 * <li>5: left border
	 * <li>6: content
	 * <li>7: right border
	 * <li>8: bottom left border
	 * <li>9: bottom border
	 * <li>10: bottom right border
	 * <li>11: outside of the frame
	 * </ul>
	 */
	public static enum Region {
		TITLE, CONTENT, LEFT, TOP, RIGHT, BOT, TOP_LEFT, TOP_RIGHT, BOT_LEFT, BOT_RIGHT, OUT
	}

	/**
	 * Default constructor
	 * 
	 * @param x abscissa in the WM
	 * @param y ordinate in the WM
	 */
	public Frame(int x, int y) {
		this.bounds = new Rectangle(100, 100);
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
	public void render() {
		int x, y, w, h;

		x = this.getX();
		y = this.getY();
		w = this.getWidth();
		h = this.getHeight();

		float alpha = 1.0f;
		if (this.isDragged()) {
			alpha = 0.5f;
		}
		if (this.isFocused()) {
			GL11.glColor4f(0.5f, 0.5f, 0.5f, alpha);
		} else {
			GL11.glColor4f(0.3f, 0.3f, 0.3f, alpha);
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

		// title-bar
		GL11.glColor4f(0.6f, 0.6f, 0.6f, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x + borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + borderWidth);
		GL11.glVertex2i(x + borderWidth, y + borderWidth);
		GL11.glEnd();

		// content
		GL11.glColor4f(0.9f, 0.9f, 0.9f, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x + borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + titlebarHeight + borderWidth);
		GL11.glVertex2i(x + w - borderWidth, y + h - borderWidth);
		GL11.glVertex2i(x + borderWidth, y + h - borderWidth);
		GL11.glEnd();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getX() {
		return this.pos.getX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
	public int getWidth() {
		return this.bounds.getWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return this.bounds.getHeight();
	}

	/**
	 * @param w the new width of this frame
	 */
	public void setWidth(int w) {
		this.bounds.setWidth(w);
	}

	/**
	 * @param h the new height of this frame
	 */
	public void setHeight(int h) {
		this.bounds.setHeight(h);
	}

	/**
	 * @param w the new width of this frame
	 * @param h the new height of this frame
	 */
	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
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

		if (mx < x) {
			return Region.OUT;
		} else if (mx < x + borderWidth) {
			px = 1;
		} else if (mx < x + this.getWidth() - borderWidth) {
			px = 2;
		} else if (mx < x + this.getWidth()) {
			px = 3;
		} else {
			return Region.OUT;
		}

		if (my < y) {
			return Region.OUT;
		} else if (my < y + borderWidth) {
			py = 1;
		} else if (my < y + borderWidth + titlebarHeight) {
			py = 2;
		} else if (my < y + this.getHeight() - borderWidth) {
			py = 3;
		} else if (my < y + this.getHeight()) {
			py = 4;
		} else {
			return Region.OUT;
		}

		switch (px) {
		case 1:
			switch (py) {
			case 1:
				return Region.TOP_LEFT;
			case 2:
			case 3:
				return Region.LEFT;
			case 4:
				return Region.BOT_LEFT;
			}
		case 2:
			switch (py) {
			case 1:
				return Region.TOP;
			case 2:
				return Region.TITLE;
			case 3:
				return Region.CONTENT;
			case 4:
				return Region.BOT;
			}
		case 3:
			switch (py) {
			case 1:
				return Region.TOP_RIGHT;
			case 2:
			case 3:
				return Region.RIGHT;
			case 4:
				return Region.BOT_RIGHT;
			}
		}

		return Region.OUT;
	}

	@Override
	public String toString() {
		return bounds.toString() + pos.toString();
	}

}
