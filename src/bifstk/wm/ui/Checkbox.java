package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;

public class Checkbox extends AbstractButton {

	private boolean checked = false;

	public Checkbox(boolean state) {
		this.checked = state;
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {

		// this widget should not scale ; if the actual size is
		// smaller it will be cut by the scissor
		int w = this.getPreferredWidth();

		float a = uiBgAlpha * alpha;

		Color.WHITE.use(a);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(w, 0);
		GL11.glVertex2i(w, w);
		GL11.glVertex2i(0, w);
		GL11.glEnd();

		Theme.getUiButtonBorderColor().use(a);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(0, 1);
		GL11.glVertex2i(w, 1);
		GL11.glVertex2i(w, 1);
		GL11.glVertex2i(w, w);
		GL11.glVertex2i(w, w);
		GL11.glVertex2i(1, w);
		GL11.glVertex2i(1, w);
		GL11.glVertex2i(0, 1);
		GL11.glEnd();

		if (this.checked) {
			int d = (int) (w * 0.2);
			Color.GRAY.use(a);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(d, d);
			GL11.glVertex2i(w - d, w - d);
			GL11.glVertex2i(w - d, d);
			GL11.glVertex2i(d, w - d);
			GL11.glEnd();
		}

	}

	/**
	 * @return true if this checkbox is checked
	 */
	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		this.checked = !this.checked;
		super.mouseUp(button, x, y);
	}

	@Override
	public int getPreferredWidth() {
		// height of a line of text
		return Fonts.getNormal().getHeight();
	}

	@Override
	public int getPreferredHeight() {
		// height of a line of text
		return Fonts.getNormal().getHeight();
	}
}
