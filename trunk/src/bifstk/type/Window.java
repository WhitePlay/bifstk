package bifstk.type;

import org.lwjgl.opengl.GL11;

public class Window extends Frame {

	public Window(int x, int y) {
		super(x, y);
	}

	public Window(int x, int y, int w, int h) {
		super(x, y, w, h);
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
}
