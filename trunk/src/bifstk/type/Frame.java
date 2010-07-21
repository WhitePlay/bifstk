package bifstk.type;

public abstract class Frame implements Drawable {

	private Rectangle bounds = null;

	private Point pos = null;

	private boolean focused = false;

	private boolean dragged = false;

	public Frame(int x, int y) {
		this.bounds = new Rectangle(100, 100);
		this.pos = new Point(x, y);
	}

	public Frame(int x, int y, int w, int h) {
		this.bounds = new Rectangle(w, h);
		this.pos = new Point(x, y);
	}

	public int getX() {
		return this.pos.getX();
	}

	public int getY() {
		return this.pos.getY();
	}

	public void setX(int x) {
		this.pos.setX(x);
	}

	public void setY(int y) {
		this.pos.setY(y);
	}

	public void setPos(int x, int y) {
		this.pos.setPos(x, y);
	}

	public int getWidth() {
		return this.bounds.getWidth();
	}

	public int getHeight() {
		return this.bounds.getHeight();
	}

	public void setWidth(int w) {
		this.bounds.setWidth(w);
	}

	public void setHeight(int h) {
		this.bounds.setHeight(h);
	}

	public void setBounds(int w, int h) {
		this.bounds.setBounds(w, h);
	}

	public boolean isFocused() {
		return this.focused;
	}

	public void setFocused(boolean focus) {
		this.focused = focus;
	}

	public boolean isDragged() {
		return this.dragged;
	}

	public void setDragged(boolean dragged) {
		this.dragged = dragged;
	}

	public boolean contains(Point p) {
		return this.contains(p.getX(), p.getY());
	}

	public boolean contains(int x, int y) {
		return bounds.contains(x - pos.getX(), y - pos.getY());
	}

	@Override
	public String toString() {
		return bounds.toString() + pos.toString();
	}

}
