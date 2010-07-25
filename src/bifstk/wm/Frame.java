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
			GL11.glColor4f(0.7f, 0.7f, 0.7f, alpha);
		} else {
			GL11.glColor4f(0.5f, 0.5f, 0.5f, alpha);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x + w, y);
		GL11.glVertex2i(x + w, y + h);
		GL11.glVertex2i(x, y + h);
		GL11.glEnd();

		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2i(x, y);
		GL11.glVertex2i(x + w, y);
		GL11.glVertex2i(x + w, y + h);
		GL11.glVertex2i(x, y + h);
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
	 * Tests whether a specific 2D coordinate is contained in the bounds of this
	 * frame
	 * 
	 * @param p a point in 2D space
	 * @return true if the provided point is contained in this frame
	 */
	public boolean contains(Point p) {
		return this.contains(p.getX(), p.getY());
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

	@Override
	public String toString() {
		return bounds.toString() + pos.toString();
	}

}
