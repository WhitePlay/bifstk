package bifstk.wm.geom;

/**
 * 2D dimension with int precision
 */
public class Rectangle {

	private int width = 0;

	private int height = 0;

	/**
	 * Default constructor
	 */
	public Rectangle() {
		this(0, 0);
	}

	/**
	 * Default constructor
	 * 
	 * @param w width
	 * @param h height
	 */
	public Rectangle(int w, int h) {
		this.width = w;
		this.height = h;
	}

	/**
	 * Copy constructor
	 * 
	 * @param rect Rectangle to copy
	 */
	public Rectangle(Rectangle rect) {
		this.width = rect.getWidth();
		this.height = rect.getHeight();
	}

	/**
	 * @return this Rectangle's width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return this Rectangle's height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @param w this Rectangle's new width
	 * @param h this Rectangle's new height
	 */
	public void setBounds(int w, int h) {
		this.width = w;
		this.height = h;
	}

	/**
	 * Copies values from the argument into this Rectangle
	 * 
	 * @param r values to copy
	 */
	public void setBounds(Rectangle r) {
		this.width = r.getWidth();
		this.height = r.getHeight();
	}

	/**
	 * @param w this Rectangle's new width
	 */
	public void setWidth(int w) {
		this.width = w;
	}

	/**
	 * @param h this Rectangle's new height
	 */
	public void setHeight(int h) {
		this.height = h;
	}

	/**
	 * Tests whether a specific 2D coordinate is contained in the bounds
	 * represented by this rectangle
	 * 
	 * @param p a point in 2D space
	 * @return true if the provided point is contained in this Rectangle
	 */
	public boolean contains(Point p) {
		return this.contains(p.getX(), p.getY());
	}

	/**
	 * Tests whether a specific 2D coordinate is contained in the bounds
	 * represented by this rectangle
	 * 
	 * @param x abscissa in 2D space
	 * @param y ordinate in 2D space
	 * @return true if the provided point is contained in this Rectangle
	 */
	public boolean contains(int x, int y) {
		return (0 < x && x <= width && 0 < y && y <= height);
	}

	@Override
	public String toString() {
		return "[" + width + "*" + height + "]";
	}
}
