package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;

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

		float[] c1 = Color.WHITE.toArray(4, a);
		int[] g1 = new int[] {
				0, 0, w, 0, w, w, 0, w
		};
		Util.draw2D(g1, c1, GL11.GL_QUADS);

		float[] c2 = Theme.getUiButtonBorderColor().toArray(8, a);
		int[] g2 = new int[] {
				0, 0, //
				w, 0, //
				w, w, //
				0, w, //
		};
		Util.draw2DLineLoop(g2, c2);

		if (this.checked) {
			float d = w * 0.2f;
			float[] c3 = Color.BLACK.toArray(4, a);
			float[] g3 = new float[] {
					d, d, //
					w - d, //
					w - d, //
					w - d, //
					d, d, //
					w - d
			};
			Util.draw2D(g3, c3, GL11.GL_LINES);
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
