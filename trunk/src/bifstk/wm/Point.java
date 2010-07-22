package bifstk.wm;

/**
 * A coordinate in 2D space, with int precision
 */
public class Point {

	/** abscissa */
	private int x = 0;
	/** ordinate */
	private int y = 0;

	/**
	 * Default constructor
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * Default constructor
	 * 
	 * @param x abscissa
	 * @param y ordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor
	 * 
	 * @param p a Point to copy
	 */
	public Point(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	/**
	 * @return the abscissa of this Point
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @return the ordinate of this Point
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @param x the new abscissa of this Point
	 * @param y the new ordinate of this Point
	 */
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param x the new abscissa of this Point
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @param y the new ordinate of this Point
	 */
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ";" + y + "]";
	}
}
