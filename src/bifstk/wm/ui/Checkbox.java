package bifstk.wm.ui;

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

		Color entryFill = Theme.getUiEntryColor();
		Color entryBorder = Theme.getUiBorderColor();

		if (Theme.isWidgetsRounded()) {
			Util.fillRoundedQuad(0, 0, pw, pw, entryFill, uiBg, a);
			Util.drawRoundedQuad(0, 0, pw, pw, entryBorder, a);
		} else {
			Util.raster().fillQuad(0, 0, pw, pw, entryFill, a);
			Util.raster().drawQuad(0, 0, pw, pw, entryBorder, a);
		}

		Color fillCol = Theme.getUiButtonColor();
		if (this.isMouseClicked()) {
			fillCol = Theme.getUiButtonClickColor();
		} else if (this.isMouseHover()) {
			fillCol = Theme.getUiButtonHoverColor();
		}

		if (this.checked) {
			int b = (int) (0.2f * pw);
			if (Theme.isWidgetsRounded()) {
				Util.fillRoundedQuad(b, b, pw - b * 2, pw - b * 2, fillCol,
						Color.TRANSP_WHITE, a);
				Util.drawRoundedQuad(b, b, pw - b * 2, pw - b * 2,
						Theme.getUiBorderColor(), a);
			} else {
				Util.raster()
						.fillQuad(b, b, pw - b * 2, pw - b * 2, fillCol, a);
				Util.raster().drawQuad(b, b, pw - b * 2, pw - b * 2,
						Theme.getUiBorderColor(), a);
			}
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
