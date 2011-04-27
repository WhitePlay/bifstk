package bifstk.wm.ui;

import bifstk.config.Fonts;
import bifstk.config.TextureLoader;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Image;
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

		Color entryFill = uiBg.add(Theme.get().uiEntryMask);
		Color entryBorder = uiBg.add(Theme.get().uiBorderMask);

		Util.raster().fillQuad(0, 0, pw, pw, entryFill, a);
		Util.raster().drawQuad(0, 0, pw, pw, entryBorder, a);

		Color fillCol = uiBg.add(Theme.get().uiCheckMask);
		if (this.isMouseClicked()) {
			fillCol = uiBg.add(Theme.get().uiCheckClickMask);
		} else if (this.isMouseHover()) {
			fillCol = uiBg.add(Theme.get().uiCheckHoverMask);
		}

		if (this.checked || this.isMouseClicked()) {
			Image img = TextureLoader.getCheckbox();
			int cx = Math.max(0, (pw - img.getWidth()) / 2);

			Util.raster().fillQuad(cx, cx, img, fillCol, a);
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
		if (button == 0)
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
