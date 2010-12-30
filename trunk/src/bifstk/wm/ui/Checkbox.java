package bifstk.wm.ui;

import org.lwjgl.opengl.GL11;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;

/**
 * Standard checkbox with two states
 * 
 * 
 */
public class Checkbox extends AbstractButton {

	private boolean checked = false;

	public Checkbox() {
		this(false);
	}

	public Checkbox(boolean state) {
		this.checked = state;
	}

	@Override
	public void render(float alpha, Color uiBg, float uiBgAlpha) {
		int w = this.getWidth();
		int h = this.getHeight();

		if (w <= 0 || h <= 0) {
			return;
		}

		// this widget should not scale ; if the actual size is
		// smaller it will be cut by the scissor
		int pw = this.getPreferredWidth(0);

		float a = uiBgAlpha * alpha;

		float[] c1 = Color.WHITE.toArray(4, a);
		int[] g1 = new int[] {
				0, 0, pw, 0, pw, pw, 0, pw
		};

		float[] c2 = Theme.getUiButtonBorderColor().toArray(8, a);
		int[] g2 = new int[] {
				0, 0, //
				pw, 0, //
				pw, pw, //
				0, pw, //
		};

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		Util.raster().draw2D(g1, c1, GL11.GL_QUADS);
		Util.raster().draw2DLineLoop(g2, c2);

		Color fillCol = Theme.getUiButtonColor();
		if (this.isMouseClicked()) {
			fillCol = Theme.getUiButtonClickColor();
		} else if (this.isMouseHover()) {
			fillCol = Theme.getUiButtonHoverColor();
		}

		if (this.checked) {
			int b = (int) (0.2f * pw);
			float[] c3 = Theme.getUiButtonBorderColor().toArray(8, a);
			float[] c4 = fillCol.toArray(4, a);
			int[] g3 = new int[] {
					b, b, //
					pw - b, b, //
					pw - b, pw - b, //
					b, pw - b //
			};
			Util.raster().draw2D(g3, c4, GL11.GL_QUADS);
			Util.raster().draw2DLineLoop(g3, c3);
		}
	}

	/**
	 * @return true if this checkbox is checked
	 */
	public boolean isChecked() {
		return this.checked;
	}

	/**
	 * @param c true if this checkbox should be checked
	 */
	public void setChecked(boolean c) {
		this.checked = c;
	}

	@Override
	public void mouseUp(int button, int x, int y) {
		this.checked = !this.checked;
		super.mouseUp(button, x, y);
	}

	@Override
	public int getPreferredWidth(int max) {
		// height of a line of text
		return Fonts.getNormal().getHeight() + 2;
	}

	@Override
	public int getPreferredHeight(int max) {
		// height of a line of text
		return Fonts.getNormal().getHeight() + 2;
	}
}
