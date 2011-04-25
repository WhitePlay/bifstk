package bifstk.wm.ui;

import bifstk.config.Fonts;
import bifstk.config.Theme;
import bifstk.gl.Color;
import bifstk.gl.Util;

/**
 * Stateless button that can display text
 * 
 */
public class Button extends AbstractButton {

	/** text to display in the button */
	private String text = null;
	/** pixel width of the string when rendered */
	private int textWidth = 0;
	/** pixel height of the string when rendered */
	private int textHeight = 0;

	/**
	 * Default constructor
	 * 
	 * @param text text to display in the button
	 */
	public Button(String text) {
		this.text = text;
		this.textWidth = Fonts.getNormal().getWidth(this.text);
		this.textHeight = Fonts.getNormal().getHeight();
	}

	@Override
	public void render(float alpha, Color uiBg, float uiAlpha) {
		int w = this.getWidth();
		int h = this.getHeight();

		if (w <= 0 || h <= 0) {
			return;
		}

		float a = uiAlpha * alpha;
		Color fillCol = null;

		if (this.isMouseClicked() && this.isMouseHover()) {
			fillCol = Theme.get().uiButtonClickColor;
		} else if (this.isMouseHover()) {
			fillCol = Theme.get().uiButtonHoverColor;
		} else {
			fillCol = Theme.get().uiButtonColor;
		}

		int lx, ly;
		if (this.textWidth < w) {
			lx = (w - this.textWidth) / 2;
		} else {
			lx = 0;
		}
		if (this.textHeight < h) {
			ly = (h - this.textHeight) / 2;
		} else {
			ly = 0;
		}
		Color fontCol = Theme.get().uiFontColor;

		Util.fillRoundedQuad(0, 0, w, h, fillCol, uiBg, a);
		Fonts.getNormal().drawString(lx, ly, this.text, fontCol, alpha);
		Util.drawRoundedQuad(0, 0, w, h, Theme.get().uiBorderColor, a);
	}

	@Override
	public int getPreferredWidth(int max) {
		return Fonts.getNormal().getWidth(this.text + "  ");
	}

	@Override
	public int getPreferredHeight(int max) {
		return Fonts.getNormal().getHeight() + 4;
	}

}
