package bifstk.wm;

public class Rectangle {

	private int width = 0;

	private int height = 0;

	public Rectangle() {
		this(0, 0);
	}

	public Rectangle(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public Rectangle(Rectangle rect) {
		this.width = rect.getWidth();
		this.height = rect.getHeight();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setBounds(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean contains(Point p) {
		return this.contains(p.getX(), p.getY());
	}

	public boolean contains(int x, int y) {
		return (0 < x && x < width && 0 < y && y < height);
	}

	@Override
	public String toString() {
		return "[" + width + "*" + height + "]";
	}
}
