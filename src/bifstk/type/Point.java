package bifstk.type;

public class Point {

	private int x = 0;
	private int y = 0;

	public Point() {
		this(0, 0);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ";" + y + "]";
	}
}
