package bifstk.wm;

import org.lwjgl.opengl.GL11;

public class Frame implements Drawable {

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
